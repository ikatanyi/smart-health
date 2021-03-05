package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalReversal;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.billing.service.PatientBillingService;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.*;
import io.smarthealth.accounting.payment.domain.*;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.accounting.payment.domain.repository.*;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.specification.ReceiptSpecification;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.domain.PriceListRepository;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.bank.domain.BankAccount;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptingService {

    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final ServicePointService servicePointService;
    private final JournalService journalEntryService;
    private final ShiftRepository shiftRepository;
    private final ReceiptRepository repository;
    private final ReceiptItemRepository receiptItemRepository;
    private final BillingService billingService;
    private final BankingService bankingService;
    private final PayerRepository payerRepository;
    private final RemittanceRepository remittanceRepository;
    private final CopaymentService copaymentService;
    private final ReceiptTransactionRepository transactionRepository;
    private final PatientBillingService patientBillingService;
    private final ReceivePaymenttRepository receivePaymenttRepository;
    private final PriceListRepository priceListRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt receivePayment(ReceivePayment data) {
        Receipt receipt = new Receipt();
        receipt.setPrepayment(Boolean.FALSE);
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(data.getPayer());
        receipt.setPaid(data.getAmount());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setTenderedAmount(data.getTenderedAmount() != null ? data.getTenderedAmount() : BigDecimal.ZERO);
        receipt.setReferenceNumber(data.getReferenceNumber());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(data.getDate());
        receipt.setDescription(data.getDescription());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);
        receipt.setPrepayment(Boolean.FALSE);
        receipt.setReceivedFrom(data.getPayer());
        data.setReceiptNo(receiptNo);
        data.setTransactionNo(trdId);

//        List<PatientBillItem> billedItems = billingService.validatedBilledItem(data);
        List<PatientBillItem> billedItems = patientBillingService.allocateBillPayment(data);
        //this I am going to surface them out and have the right de
        billedItems.stream()
                .forEach(item -> {
                    if (item.getItem().getCategory() == ItemCategory.CoPay) {
                        copaymentService.createCopayment(data.getVisitNumber(), data.getPatientNumber(), data.getAmount(), receiptNo);
                    }
                    receipt.addReceiptItem(
                            new ReceiptItem(
                                    item, item.getQuantity(),
                                    toBigDecimal(item.getPrice()),
                                    toBigDecimal(item.getDiscount()),
                                    toBigDecimal(item.getTaxes()),
                                    toBigDecimal((item.getAmount() - (item.getDiscount() != null ? item.getDiscount() : 0D)))
                            ));
                });

        List<ReceiptMethod> toBank = new ArrayList<>();

        if (!data.getPayment().isEmpty()) {
            receipt.addTransaction(
                    data.getPayment()
                            .stream()
                            .map(t -> {
                                if (StringUtils.isNotBlank(t.getAccountNumber())) {
                                    toBank.add(t);
                                }
                                return createPaymentTransaction(t);
                            })
                            .filter(x -> x.getAmount() != null)
                            .collect(Collectors.toList())
            );
        }

        Receipt savedReceipt = repository.save(receipt); //save the payment
        //bank payments
        depositToBank(savedReceipt, toBank);

        switch (data.getType()) {
            case Patient:
                journalEntryService.save(toJournalReceipting(savedReceipt, billedItems, toBank));
                break;
            case Insurance:
                Optional<Payer> payer = payerRepository.findById(data.getPayerId());
                if (payer.isPresent()) {
                    Remittance remittance = new Remittance(payer.get(), savedReceipt);
                    remittanceRepository.save(remittance);
                    journalEntryService.save(toJournalRemittance(payer.get(), receipt, toBank));
                }
                break;

            case Others:
                break;
            default:
        }

        return savedReceipt;
    }

    public Optional<Receipt> getPayment(Long id) {
        return repository.findById(id);
    }

    public Receipt getPaymentOrThrow(Long id) {
        return getPayment(id)
                .orElseThrow(() -> APIException.notFound("Payment with Id {0} Not Found", id));
    }

    public Receipt getPaymentByReceiptNumber(String receiptNo) {
        return repository.findByReceiptNo(receiptNo)
                .orElseThrow(() -> APIException.notFound("Payment with Receipt Number {0} Not Found", receiptNo));
    }

    public List<CashierShift> getCashierShift(String shiftNo, Long cashierId) {
        return receiptItemRepository.findTotalByCashierShift(shiftNo, cashierId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void voidPayment(String receiptNo) { 
        Receipt receipt = getPaymentByReceiptNumber(receiptNo);

        repository.voidPayment(SecurityUtils.getCurrentUserLogin().orElse("system"), receipt.getId());
        Optional<JournalEntry> journalEntry = journalEntryService.findJournalByTransactionNo(receipt.getTransactionNo());
        if (journalEntry.isPresent()) {
            journalEntryService.reverseJournal(journalEntry.get().getId(), new JournalReversal(receipt.getTransactionDate().toLocalDate(), "Receipt Cancelation - Receipt No. " + receipt.getTransactionNo(), null));
        }
    }

    @Transactional
    public Receipt receiptAdjustment(String receiptNo, List<ReceiptItemData> receiptItems) {
        Receipt receipt = getPaymentByReceiptNumber(receiptNo);

        List<ReceiptItem> lists = receiptItems
                .stream()
                .map(x -> {
                    ReceiptItem p = receiptItemRepository.findById(x.getId()).orElseThrow(() -> APIException.notFound("No Receipt Item Found with the Id {0}", x.getId()));
                    p.setVoided(Boolean.TRUE);
                    p.setVoidedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                    p.setVoidedDate(LocalDateTime.now());
                    receipt.setRefundedAmount(receipt.getRefundedAmount().add(p.getAmountPaid()));
                    return p;
                })
                .collect(Collectors.toList());

        //then cancel the receipts and adjust the 
        repository.save(receipt);
        receiptItemRepository.saveAll(lists);
        return receipt;
    }

    @Transactional
    public Receipt receiptAdjustmentMethod(String receiptNo, List<ReceiptMethod> method) {
        Receipt receipt = getPaymentByReceiptNumber(receiptNo);
        receipt.getTransactions().forEach(x -> transactionRepository.delete(x));

        List<ReceiptTransaction> trans = method.stream()
                .map(data -> createPaymentTransaction(data))
                .collect(Collectors.toList());
        receipt.addTransaction(trans);
        Receipt savedReceipt = repository.save(receipt);
        return savedReceipt;
    }

    public Page<Receipt> getPayments(String payee, String receiptNo, String transactionNo, String shiftNo, Long servicePointId, Long cashierId, DateRange range, Boolean prepaid, Pageable page) {
        Specification<Receipt> spec = ReceiptSpecification.createSpecification(payee, receiptNo, transactionNo, shiftNo, servicePointId, cashierId, range, prepaid);
        return repository.findAll(spec, page);
    }

    public Page<ReceiptItem> getPaymentItems(Long servicePointId, DateRange range, Pageable page) {
        Specification<ReceiptItem> spec = ReceiptSpecification.createReceiptItemSpecification(servicePointId, range);
        return receiptItemRepository.findAll(spec, page);
    }

    public Page<ReceiptItem> getVoidedItems(Long servicePointId, String patientNumber, String itemCode, Boolean voided, DateRange range, Pageable page) {
        Specification<ReceiptItem> spec = ReceiptSpecification.createVoidedReceiptItemSpecification(servicePointId, patientNumber, itemCode, voided, range);
        return receiptItemRepository.findAll(spec, page);
    }

    public Page<ReceiptTransaction> getTransactions(String method, String receiptNo, TrnxType type, DateRange range, Pageable page) {
        Specification<ReceiptTransaction> spec = ReceiptSpecification.createSpecification(method, receiptNo, type, range);
        return transactionRepository.findAll(spec, page);
    }

    private ReceiptTransaction createPaymentTransaction(ReceiptMethod data) {
        ReceiptTransaction trans = new ReceiptTransaction();
        trans.setCurrency(data.getCurrency());
        trans.setDatetime(LocalDateTime.now());
        trans.setAmount(data.getAmount());
        trans.setMethod(data.getMethod());
        trans.setReference(data.getReference());
        trans.setType(TrnxType.Payment);
        return trans;
    }

    private void depositToBank(Receipt receipt, List<ReceiptMethod> methods) {

        List<Banking> toBanking = new ArrayList<>();
        methods.forEach(x -> {
            Optional<BankAccount> bankAccount = bankingService.findBankAccountByNumber(x.getAccountNumber());
            if (bankAccount.isPresent()) {
                Banking bank = Banking.deposit(bankAccount.get(), receipt, x.getAmount());
                toBanking.add(bank);
            }
        });
        bankingService.save(toBanking);
    }

    private JournalEntry toJournalReceipting(Receipt payment, List<PatientBillItem> billedItems, List<ReceiptMethod> methods) {

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);

        Optional<FinancialActivityAccount> copay = activityAccountRepository.findByFinancialActivity(FinancialActivity.Copayment);

        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Receipt Control Account is Not Mapped For Transaction");
        }

        if (!copay.isPresent()) {
            throw APIException.badRequest("Copayment Account is Not Mapped For Transaction");
        }

//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> items = new ArrayList<>();

        if (!billedItems.isEmpty()) {

            Map<Long, List<PatientBillItem>> map = billedItems
                    .stream()
                    .filter(x -> x.getItem().getCategory() != ItemCategory.CoPay)
                    .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                            Collectors.toList()
                    //                            Collectors.summingDouble(PatientBillItem::getAmount)
                    )
                    );
            //then here since we making a revenue
            map.forEach((k, v) -> {
                //revenue
                ServicePoint srv = servicePointService.getServicePoint(k);
                String narration = "Receipting for " + srv.getName();
                Account credit = srv.getIncomeAccount();//account receivable full amount
//                BigDecimal amount = BigDecimal.valueOf(v);
                Double subTotal = 0D;
                Double discount = 0D;
                Double taxes = 0D;
                for (PatientBillItem b : v) {
                    subTotal += b.getNetAmount(); //
                    discount += b.getDiscount();
                    taxes += b.getTaxes();
                }

                //amount less discount
                BigDecimal amount = BigDecimal.valueOf(subTotal).add(BigDecimal.valueOf(discount));
                if (discount > 0) {
                    FinancialActivityAccount debitDiscountAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Discount_Allowed)
                            .orElseThrow(() -> APIException.badRequest("Discount Account is Not Mapped For Transaction"));
                    items.add(new JournalEntryItem(debitDiscountAccount.getAccount(), "Sales Discount - " + srv.getName(), BigDecimal.valueOf(discount), BigDecimal.ZERO));
                }
//                if (taxes > 0) {
//                    FinancialActivityAccount debitDiscountAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Tax_Payable)
//                            .orElseThrow(() -> APIException.badRequest("Discount Account is Not Mapped For Transaction"));
//                    items.add(new JournalEntryItem(debitDiscountAccount.getAccount(), "Sales Discount - " + srv.getName(), BigDecimal.valueOf(discount), BigDecimal.ZERO));
//                }
                items.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, BigDecimal.valueOf(subTotal), BigDecimal.ZERO));
                items.add(new JournalEntryItem(credit, narration, BigDecimal.ZERO, amount));
            });
            //

            //expenses
            Map<Long, Double> inventory = billedItems
                    .stream()
                    .filter(x -> x.getItem().isInventoryItem())
                    .collect(
                            Collectors.groupingBy(PatientBillItem::getServicePointId,
                                    Collectors.summingDouble(x -> (x.getItem().getCostRate().doubleValue() * x.getQuantity())))
                    );
            if (!inventory.isEmpty()) {
                inventory.forEach((k, v) -> {
                    //revenue
                    ServicePoint srv = servicePointService.getServicePoint(k);
                    String narration = "Expensing Inventory for " + srv.getName();
                     BigDecimal amount = BigDecimal.valueOf(v);
                    items.add(new JournalEntryItem(srv.getExpenseAccount(), narration, amount, BigDecimal.ZERO));
                    items.add(new JournalEntryItem(srv.getInventoryAssetAccount(), narration, BigDecimal.ZERO, amount));
                });
            }
        }

        billedItems.stream()
                .filter(x -> x.getItem().getCategory() == ItemCategory.CoPay)
                .forEach(x -> {
                    String narration = "Copayment Receipting for - " + x.getPatientBill().getPatient().getPatientNumber();
                    BigDecimal amount = BigDecimal.valueOf(x.getAmount());
                    items.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, amount, BigDecimal.ZERO));
                    items.add(new JournalEntryItem(copay.get().getAccount(), narration, BigDecimal.ZERO, amount));
                });
        //
        if (!methods.isEmpty()) {
            methods.stream()
                    .forEach(method -> {
                        if (StringUtils.isNotBlank(method.getAccountNumber())) {
                            Optional<BankAccount> bank = bankingService.findBankAccountByNumber(method.getAccountNumber());
                            if (bank.isPresent()) {
                                BankAccount account = bank.get();
                                String narration = "Banking Patient Receipt number - " + payment.getReceiptNo();
                                BigDecimal amount = method.getAmount();
                                items.add(new JournalEntryItem(account.getLedgerAccount(), narration, amount, BigDecimal.ZERO));
                                items.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, BigDecimal.ZERO, amount));
                            }
                        }
                    });

        }

        String description = payment.getDescription() + " - Receipt no. " + payment.getReceiptNo();
        JournalEntry toSave = new JournalEntry(payment.getTransactionDate().toLocalDate(), description, items);
        toSave.setTransactionType(TransactionType.Receipting);
        toSave.setTransactionNo(payment.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    private JournalEntry toJournalRemittance(Payer payer, Receipt payment, List<ReceiptMethod> methods) {

        if (payer == null || payer.getDebitAccount() == null) {
            return null;
        }
        List<JournalEntryItem> items = new ArrayList<>();
        if (!methods.isEmpty()) {
            methods.stream()
                    .forEach(method -> {
                        //check if payment method has a bank
                        if (StringUtils.isNotBlank(method.getAccountNumber())) {
                            Optional<BankAccount> bank = bankingService.findBankAccountByNumber(method.getAccountNumber());
                            if (bank.isPresent()) {
                                BankAccount account = bank.get();
                                String narration = "Banking Remittance Number - " + method.getReference();
                                BigDecimal amount = method.getAmount();
                                items.add(new JournalEntryItem(account.getLedgerAccount(), narration, amount, BigDecimal.ZERO));
                                items.add(new JournalEntryItem(payer.getDebitAccount(), narration, BigDecimal.ZERO, amount));
                            }
                        }
                    });

        }

        String description = payment.getDescription() + " - Receipt no. " + payment.getReceiptNo();
        JournalEntry toSave = new JournalEntry(payment.getTransactionDate().toLocalDate(), description, items);
        toSave.setTransactionType(TransactionType.Remittance);
        toSave.setTransactionNo(payment.getTransactionNo());
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    private BigDecimal toBigDecimal(Double val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(val);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt receiptCopay(ReceiptInvoiceData data) {

        Receipt receipt = new Receipt();
        receipt.setAmount(data.getAmount());
        receipt.setPayer(data.getPatientName());
        receipt.setPaid(data.getAmount());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setTenderedAmount(data.getAmount());
        receipt.setReferenceNumber(data.getReference());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(LocalDateTime.now());
        receipt.setDescription(data.getDescription());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);
        receipt.setPrepayment(Boolean.FALSE);
        receipt.setReceivedFrom(data.getPatientName());
        Receipt savedReceipt = repository.save(receipt);

        patientBillingService.createReceiptItem(data.getPatientNumber(),data.getPatientName(),data.getVisitNumber(),data.getAmount().doubleValue(),ItemCategory.CoPay,false, savedReceipt.getReceiptNo());

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
        Optional<FinancialActivityAccount> copay = activityAccountRepository.findByFinancialActivity(FinancialActivity.Copayment);

        String desc= String.format("Copayment Receipting. Patient %s Receipt No. %s", data.getPatientNumber(), savedReceipt.getReceiptNo());

        List<JournalEntryItem> items = new ArrayList<>();
        BigDecimal amount = savedReceipt.getAmount();
        items.add(new JournalEntryItem(debitAccount.get().getAccount(), desc, amount, BigDecimal.ZERO));
        items.add(new JournalEntryItem(copay.get().getAccount(), desc, BigDecimal.ZERO, amount));

        JournalEntry copayJournal = new JournalEntry(savedReceipt.getTransactionDate().toLocalDate(), desc, items);
        copayJournal.setTransactionType(TransactionType.Receipting);
        copayJournal.setTransactionNo(savedReceipt.getTransactionNo());
        copayJournal.setStatus(JournalState.PENDING);

        journalEntryService.save(copayJournal);

        return receipt;
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt receiptPatient(PatientReceipt data) {

        Receipt receipt = new Receipt();
        receipt.setAmount(data.getAmount());
        receipt.setPayer(data.getPatientName());
        receipt.setPaid(data.getAmount());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setTenderedAmount(data.getAmount());
        receipt.setReferenceNumber(data.getReference());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(LocalDateTime.now());
        receipt.setDescription(data.getDescription());

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);
        receipt.setPrepayment(data.getReceiptType() == PatientReceipt.Type.Deposit);
        receipt.setReceivedFrom(data.getPatientName());

        Receipt savedReceipt = repository.save(receipt);

        ItemCategory category = (data.getReceiptType() == PatientReceipt.Type.Deposit) ? ItemCategory.CoPay : ItemCategory.Receipt;

        patientBillingService.createReceiptItem(data.getPatientNumber(),data.getPatientName(),data.getVisitNumber(),data.getAmount().doubleValue(),category,false, savedReceipt.getReceiptNo());

        if (data.getReceiptType() == PatientReceipt.Type.Deposit) {
            PaymentDeposit deposit = new PaymentDeposit();
            deposit.setAmount(savedReceipt.getAmount());
            deposit.setBalance(savedReceipt.getAmount());
            deposit.setCustomerType(PayerType.Patient);
            deposit.setDescription(data.getDescription());
            deposit.setPaymentDate(data.getDate());
            deposit.setPaymentMethod(data.getPaymentMethod());
            deposit.setReceipt(savedReceipt);
            deposit.setReference(data.getReference());
            deposit.setTransactionNo(savedReceipt.getTransactionNo());
            deposit.setType(RecordType.Deposit);

            receivePaymenttRepository.save(deposit);
        }else{
            Optional<PriceList> priceList = priceListRepository.getPriceListByItemCategory(ItemCategory.Admission);
            if(priceList.isPresent()) {
                Account creditAccount = priceList.get().getServicePoint().getIncomeAccount();
                Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);

                String desc = String.format("Medical Bills Receipting. Patient %s Receipt No. %s", data.getPatientNumber(), savedReceipt.getReceiptNo());

                List<JournalEntryItem> items = new ArrayList<>();
                BigDecimal amount = savedReceipt.getAmount();
                items.add(new JournalEntryItem(debitAccount.get().getAccount(), desc, amount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(creditAccount, desc, BigDecimal.ZERO, amount));

                JournalEntry copayJournal = new JournalEntry(savedReceipt.getTransactionDate().toLocalDate(), desc, items);
                copayJournal.setTransactionType(TransactionType.Receipting);
                copayJournal.setTransactionNo(savedReceipt.getTransactionNo());
                copayJournal.setStatus(JournalState.PENDING);
                journalEntryService.save(copayJournal);
            }
        }

        return savedReceipt;
    }
}
