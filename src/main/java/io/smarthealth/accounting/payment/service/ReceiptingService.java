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
import io.smarthealth.accounting.billing.data.VoidReceipt;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.billing.service.PatientBillingService;
import io.smarthealth.accounting.cashier.data.CashierShift;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.*;
import io.smarthealth.accounting.payment.domain.*;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.accounting.payment.domain.repository.*;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.specification.ReceiptSpecification;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.domain.PriceListRepository;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegrationRepository;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.PayerRepository;
import io.smarthealth.infrastructure.common.IntegrationStatus;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.integration.domain.MobileMoneyResponse;
import io.smarthealth.integration.domain.MobileMoneyResponseRepository;
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
    private final DoctorsRequestRepository doctorsRequestRepository;
    private final MobileMoneyIntegrationRepository mobileMoneyIntegrationRepository;
    private final MobileMoneyResponseRepository mobileMoneyResponseRepository;

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
        receipt.setType(Receipt.Type.Payment);
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

        List<ReceiptMethod> receiptMethods = new ArrayList<>();


        if (!data.getPayment().isEmpty()) {
            //TODO read Simon's personal opinion and comments.
            //Comment: this method of intercepting child classes is so limiting in manipulating sub-child classes
            // attributes.
            receipt.addTransaction(
                    data.getPayment()
                            .stream()
                            .map(t -> {
//                                if (StringUtils.isNotBlank(t.getAccountNumber())) {
                                receiptMethods.add(t);
//                                }
                                return createPaymentTransaction(t);
                            })
                            .filter(x -> x.getAmount() != null)
                            .collect(Collectors.toList())
            );
        }
        //TODO this is a quick hack
        Optional<PatientBillItem> pbi = billedItems.stream().filter(x -> x.getEntryType() == BillEntryType.Debit).findFirst();
        if (pbi.isPresent()) {
            if (pbi.get().getPatientBill().getVisit() != null) {
                receipt.setVisitType(pbi.get().getPatientBill().getVisit().getVisitType());
            } else {
                receipt.setVisitType(VisitEnum.VisitType.Outpatient);
            }
        }

        Receipt savedReceipt = repository.save(receipt); //save the payment
        //TODO read Simon's personal opinion and comments and a question on depositToBank.
        //depositToBank is redundant, because a bank is linked to the gl accounts. subject to confirmation on how
        // the cash book reconciliation is done

        //bank payments
        depositToBank(savedReceipt, receiptMethods);

        switch (data.getType()) {
            case Patient:
                journalEntryService.save(toJournalReceipting(savedReceipt, billedItems, receiptMethods, data));
                break;
            case Insurance:
                Optional<Payer> payer = payerRepository.findById(data.getPayerId());
                if (payer.isPresent()) {
                    Remittance remittance = new Remittance(payer.get(), savedReceipt);
                    remittanceRepository.save(remittance);
                    journalEntryService.save(toJournalRemittance(payer.get(), receipt, receiptMethods));
                }
                break;

            case Others:
                break;
            default:
        }

        updateDoctorRequests(billedItems);

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
    public Receipt voidPayment(VoidReceipt data) {
        Receipt receipt = getPaymentByReceiptNumber(data.getReceiptNo());

        repository.voidPayment(SecurityUtils.getCurrentUserLogin().orElse("system"), receipt.getId(), data.getComments());
        Optional<JournalEntry> journalEntry = journalEntryService.findJournalByTransactionNo(receipt.getTransactionNo());
        if (journalEntry.isPresent()) {
            journalEntryService.reverseJournal(journalEntry.get().getId(), new JournalReversal(receipt.getTransactionDate().toLocalDate(), "Receipt Cancelation - Receipt No. " + receipt.getTransactionNo(), null));
        }
        //determine the transactions done and reverse them too
        patientBillingService.reverseBillItemsPaid(data.getReceiptNo());
        return getPaymentOrThrow(receipt.getId());
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
        //post journals

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

    public Page<Receipt> getPayments(String payee, String receiptNo, String transactionNo, String shiftNo, Long servicePointId, Long cashierId, DateRange range, Boolean prepaid, VisitEnum.VisitType visitType, Pageable page) {
        Specification<Receipt> spec = ReceiptSpecification.createSpecification(payee, receiptNo, transactionNo, shiftNo, servicePointId, cashierId, range, prepaid, visitType);
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
        /*
        method: MOBILE MONEY
reference: transID
type: Safaricom
amount:50
         */
        if (data.getMethod().equals(ReceiptAndPaymentMethod.Mobile_Money)) {
            //check if mobile money type selected is integrated with the gateway
            MobileMoneyIntegration mmc = mobileMoneyIntegrationRepository.findById(data.getReferenceAccount()).orElseThrow(() -> APIException.notFound("Mobile money configuration identified by {0} not found ", data.getReferenceAccount()));
            //if mobile money type is integrated with the gateway, mark money response entity as bill allocated
            if (mmc.getStatus().equals(IntegrationStatus.InActive)) {
                return trans;
            }
            if (mmc.getStatus().equals(IntegrationStatus.Active)) {
                //if integration is active, find money response identified by the reference at hand
                MobileMoneyResponse mmr = mobileMoneyResponseRepository.findByTransID(data.getReference()).orElseThrow(() -> APIException.notFound("Trx number identified by {0} not found ", data.getReference()));
                mmr.setPatientBillEffected(Boolean.TRUE);
                mobileMoneyResponseRepository.save(mmr);

                //affect accounts accordingly

            }

        }
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

    private JournalEntry toJournalReceipting(Receipt payment, List<PatientBillItem> billedItems,
                                             List<ReceiptMethod> receiptMethods, ReceivePayment receivePaymentData) {

//        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);

        Optional<FinancialActivityAccount> copay = activityAccountRepository.findByFinancialActivity(FinancialActivity.Copayment);

//        if (!debitAccount.isPresent()) {
//            throw APIException.badRequest("Receipt Control Account is Not Mapped For Transaction");
//        }

        if (!copay.isPresent()) {
            throw APIException.badRequest("Copayment Account is Not Mapped For Transaction");
        }

//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> journalEntryItems = new ArrayList<>();

        if (!billedItems.isEmpty()) {

            Map<Long, List<PatientBillItem>> patientBillItems = billedItems
                    .stream()
                    .filter(x -> x.getItem().getCategory() != ItemCategory.CoPay)
                    .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                            Collectors.toList()
                            //                            Collectors.summingDouble(PatientBillItem::getAmount)
                            )
                    );
            //then here since we making a revenue
            //patientBillItems list has no copay in this case
            patientBillItems.forEach((k, v) -> {
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
                    journalEntryItems.add(new JournalEntryItem(debitDiscountAccount.getAccount(), "Sales Discount - " + srv.getName(), BigDecimal.valueOf(discount), BigDecimal.ZERO));
                }
//                if (taxes > 0) {
//                    FinancialActivityAccount debitDiscountAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Tax_Payable)
//                            .orElseThrow(() -> APIException.badRequest("Discount Account is Not Mapped For Transaction"));
//                    items.add(new JournalEntryItem(debitDiscountAccount.getAccount(), "Sales Discount - " + srv.getName(), BigDecimal.valueOf(discount), BigDecimal.ZERO));
//                }

                //this is the cash account - Bank/Cash/Card/Mobile_Money
                //journalEntryItems.add(new JournalEntryItem(debitAccount.get().getAccount(), narration,BigDecimal.valueOf(subTotal), BigDecimal.ZERO));
                journalEntryItems.add(new JournalEntryItem(credit, narration, BigDecimal.ZERO, amount));
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
                    journalEntryItems.add(new JournalEntryItem(srv.getExpenseAccount(), narration, amount, BigDecimal.ZERO));
                    journalEntryItems.add(new JournalEntryItem(srv.getInventoryAssetAccount(), narration, BigDecimal.ZERO, amount));
                });
            }
        }

//handle copay item based journal entries
        billedItems.stream()
                .filter(x -> x.getItem().getCategory() == ItemCategory.CoPay)
                .forEach(x -> {
                    String narration = "Copayment Receipting for - " + x.getPatientBill().getPatient().getPatientNumber();
                    BigDecimal amount = BigDecimal.valueOf(x.getAmount());
//                    journalEntryItems.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, amount, BigDecimal.ZERO));
                    journalEntryItems.add(new JournalEntryItem(copay.get().getAccount(), narration, BigDecimal.ZERO, amount));
                });
        //
        if (!receiptMethods.isEmpty()) {
            receiptMethods.stream()
                    .forEach(method -> {
                        //this is the cash accounts - Bank/Cash/Card/Mobile_Money
                        if (method.getMethod().equals(ReceiptAndPaymentMethod.Cash)) {
                            Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
                            if (!debitAccount.isPresent()) {
                                throw APIException.badRequest("Receipt Control Account is Not Mapped For Transaction");
                            }
                            String narration = "Patient receipt: Patient No. " + receivePaymentData.getPatientNumber();
                            journalEntryItems.add(new JournalEntryItem(debitAccount.get().getAccount(), narration,
                                    method.getAmount(), BigDecimal.ZERO));
                        }
                        if (method.getMethod().equals(ReceiptAndPaymentMethod.Bank)) {
                            Optional<BankAccount> bank = bankingService.findBankById(method.getReferenceAccount());
                            if (bank.isPresent()) {
                                BankAccount account = bank.get();
                                String narration = "Banking Patient Receipt number - " + payment.getReceiptNo();
                                BigDecimal amount = method.getAmount();
                                journalEntryItems.add(new JournalEntryItem(account.getLedgerAccount(), narration, amount, BigDecimal.ZERO));
//                                journalEntryItems.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, BigDecimal.ZERO, amount));
                            }
                        }

                        if (method.getMethod().equals(ReceiptAndPaymentMethod.Mobile_Money)) {
                            Optional<MobileMoneyIntegration> mmprovider = mobileMoneyIntegrationRepository.findById(method.getReferenceAccount());
                            if (mmprovider.isPresent()) {
                                MobileMoneyIntegration account = mmprovider.get();
                                String narration = "Banking Patient Receipt number - " + payment.getReceiptNo();
                                BigDecimal amount = method.getAmount();
                                journalEntryItems.add(new JournalEntryItem(account.getCashAccount(), narration, amount, BigDecimal.ZERO));
//                                journalEntryItems.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, BigDecimal.ZERO, amount));
                            }
                        }
                        if (method.getMethod().equals(ReceiptAndPaymentMethod.Card)) {
                            throw APIException.badRequest("mmhm! Card payments settings not yet configured. You are " +
                                    "quite ambitious. ");
                        }
                    });

        }

        String description = payment.getDescription() + " - Receipt no. " + payment.getReceiptNo();
        JournalEntry toSave = new JournalEntry(payment.getTransactionDate().toLocalDate(), description, journalEntryItems);
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
        receipt.setType(Receipt.Type.Payment);
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

        patientBillingService.createReceiptItem(data.getPatientNumber(), data.getPatientName(), data.getVisitNumber(), data.getAmount().doubleValue(), ItemCategory.CoPay, false, savedReceipt.getReceiptNo());

        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);
        Optional<FinancialActivityAccount> copay = activityAccountRepository.findByFinancialActivity(FinancialActivity.Copayment);

        String desc = String.format("Copayment Receipting. Patient %s Receipt No. %s", data.getPatientNumber(), savedReceipt.getReceiptNo());

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
        receipt.setType(Receipt.Type.Payment);
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

        patientBillingService.createReceiptItem(data.getPatientNumber(), data.getPatientName(), data.getVisitNumber(), data.getAmount().doubleValue(), category, false, savedReceipt.getReceiptNo());

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
        } else {
            Optional<PriceList> priceList = priceListRepository.getPriceListByItemCategory(ItemCategory.Admission);
            if (priceList.isPresent()) {
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

    public void updateDoctorRequests(List<PatientBillItem> billedItems) {
        billedItems.stream()
                .filter(x -> x.getRequestReference() != null)
                .forEach(req -> doctorsRequestRepository.updateBilledAndPaidDoctorRequest(req.getRequestReference()));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt refundReceipt(RefundData data) {

        //get the receipt to refund
        Optional<Receipt> optReceipt = repository.findByReceiptNo(data.getReceiptNo());
        if (optReceipt.isPresent()) {

            Receipt currentReceipt = optReceipt.get();
            currentReceipt.setRefundedAmount(currentReceipt.getRefundedAmount().add(data.getRefundAmount()));
            currentReceipt.setComments(data.getComments());
            repository.save(currentReceipt);

            String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
            String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

            Receipt receipt = new Receipt();
            receipt.setAmount(data.getRefundAmount());
            receipt.setPayer(currentReceipt.getPayer());
            receipt.setDescription("Receipt Refund (" + currentReceipt.getReceiptNo() + ")");
            receipt.setPaid(data.getRefundAmount());
            receipt.setPaymentMethod(data.getPaymentMethod());
            receipt.setTenderedAmount(data.getRefundAmount());
            receipt.setReferenceNumber(data.getReferenceNumber());
            receipt.setRefundedAmount(BigDecimal.ZERO);
            receipt.setType(Receipt.Type.Refund);
            if (data.getShiftNo() != null) {
                Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
                receipt.setShift(shift);
            }
            receipt.setTransactionDate(LocalDateTime.now());
            receipt.setDescription(data.getComments());
            receipt.setTransactionNo(trdId);
            receipt.setReceiptNo(receiptNo);
            receipt.setPrepayment(Boolean.FALSE);
            receipt.setReceivedFrom(data.getPayer());
            Receipt savedReceipt = repository.save(receipt);

            //do the posting
            List<PatientBillItem> items = data.getItems()
                    .stream()
                    .map(x -> {
                        ReceiptItem p = receiptItemRepository.findById(x.getId()).orElseThrow(() -> APIException.notFound("No Receipt Item Found with the Id {0}", x.getId()));
                        p.setVoided(Boolean.TRUE);
                        p.setVoidedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                        p.setVoidedDate(LocalDateTime.now());
                        receiptItemRepository.save(p);
                        PatientBillItem itm = p.getItem();
                        itm.setStatus(BillStatus.Canceled);
                        return itm;
                    }).collect(Collectors.toList());

            patientBillingService.createReceiptRefund(savedReceipt, items);

            return savedReceipt;
        }
        return null;
    }
}
