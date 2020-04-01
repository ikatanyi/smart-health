package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.cashier.domain.ShiftRepository;
import io.smarthealth.accounting.payment.data.ReceiptMethod;
import io.smarthealth.accounting.payment.data.ReceivePayment;
import io.smarthealth.accounting.payment.domain.Banking;
import io.smarthealth.accounting.payment.domain.Receipt;
import io.smarthealth.accounting.payment.domain.ReceiptItem;
import io.smarthealth.accounting.payment.domain.ReceiptTransaction;
import io.smarthealth.accounting.payment.domain.Remittance;
import io.smarthealth.accounting.payment.domain.RemittanceRepository;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.accounting.payment.domain.specification.ReceiptSpecification;
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
import java.time.LocalDate;
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
import io.smarthealth.accounting.payment.domain.ReceiptRepository;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceivePaymentService {

    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final ServicePointService servicePointService;
    private final JournalService journalEntryService;
    private final ShiftRepository shiftRepository;
    private final ReceiptRepository repository;
    private final BillingService billingService;
    private final BankingService bankingService;
    private final PayerRepository payerRepository;
    private final RemittanceRepository remittanceRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Receipt receivePayment(ReceivePayment data) {
        Receipt receipt = new Receipt();
        receipt.setAmount(data.getAmount());
        receipt.setCurrency(data.getCurrency());
        receipt.setPayer(data.getPayer());
        receipt.setPaymentMethod(data.getPaymentMethod());
        receipt.setReferenceNumber(data.getReferenceNumber());
        receipt.setRefundedAmount(BigDecimal.ZERO);
        if (data.getShiftNo() != null) {
            Shift shift = shiftRepository.findByShiftNo(data.getShiftNo()).orElse(null);
            receipt.setShift(shift);
        }
        receipt.setTransactionDate(data.getDate());

        if (null == data.getType()) {
            receipt.setDescription("Other Payment");
        } else {
            switch (data.getType()) {
                case Patient:
                    receipt.setDescription("Patient Payment");
                    break;
                case Insurance:
                    //find the payer and add them
                    receipt.setDescription("Insurance Payment");
                    break;
                default:
                    receipt.setDescription("Other Payment");
                    break;
            }
        }
        List<ReceiptMethod> toBank = new ArrayList<>();

        if (!data.getPayment().isEmpty()) {
            receipt.addTransaction(
                    data.getPayment()
                            .stream().map(t -> {
                                if (StringUtils.isNotBlank(t.getAccountNumber())) {
                                    toBank.add(t);
                                }
                                return createPaymentTransaction(t);
                            })
                            .collect(Collectors.toList())
            );
        }

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receiptNo = sequenceNumberService.next(1L, Sequences.Receipt.name());

        receipt.setTransactionNo(trdId);
        receipt.setReceiptNo(receiptNo);
        
        List<PatientBillItem> billedItems = new ArrayList<>();
        //update payments for the bills
        if (data.getBillItems()!=null && !data.getBillItems().isEmpty()) {
            if (!data.getBillItems().isEmpty()) {
                data.getBillItems()
                        .stream()
                        .forEach(x -> {
                            PatientBillItem item = billingService.findBillItemById(x.getBillItemId());
                            BigDecimal bal = BigDecimal.valueOf(item.getAmount()).subtract(x.getAmount());
                            item.setPaid(Boolean.TRUE);
                            item.setStatus(BillStatus.Final);
                            item.setPaymentReference(receiptNo);
                            item.setBalance(bal.doubleValue());
                            PatientBillItem i = billingService.updateBillItem(item);

                            billedItems.add(i);
                            //update the bill as 
                            receipt.addReceiptItem(new ReceiptItem(item, BigDecimal.valueOf(item.getAmount())));
                        });
            }
        }

        Receipt savedPayment = repository.save(receipt); //save the payment
        //bank payments
        depositToBank(savedPayment, toBank);

        

        switch (data.getType()) {
            case Patient:
                journalEntryService.save(toJournalReceipting(receipt, billedItems, toBank));
                break;
            case Insurance:
                Optional<Payer> payer = payerRepository.findById(data.getPayerId());
                if (payer.isPresent()) {
                    Remittance remittance = new Remittance(payer.get(), savedPayment);
                    remittanceRepository.save(remittance);
                    journalEntryService.save(toJournalRemittance(payer.get(), receipt, toBank));
                }
                break;
            case Others:
                break;
            default:
        }

        return savedPayment;
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

    public void voidPayment(String receiptNo) {
        Receipt payment = getPaymentByReceiptNumber(receiptNo);
        repository.voidPayment(SecurityUtils.getCurrentUserLogin().orElse("system"), payment.getId());
    }

    public Page<Receipt> getPayments(String payee, String receiptNo, String transactionNo, String shiftNo, Long cashierId, DateRange range, Pageable page) {
        Specification<Receipt> spec = ReceiptSpecification.createSpecification(payee, receiptNo, transactionNo, shiftNo, cashierId, range);
        return repository.findAll(spec, page);
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

        if (!debitAccount.isPresent()) {
            throw APIException.badRequest("Receipt Control Account is Not Mapped");
        }
//        String debitAcc = debitAccount.get().getAccount().getIdentifier();
        List<JournalEntryItem> items = new ArrayList<>();

        if (!billedItems.isEmpty()) {
            Map<Long, Double> map = billedItems
                    .stream()
                    .collect(Collectors.groupingBy(PatientBillItem::getServicePointId,
                            Collectors.summingDouble(PatientBillItem::getAmount)
                    )
                    );
            //then here since we making a revenue
            map.forEach((k, v) -> {
                //revenue
                ServicePoint srv = servicePointService.getServicePoint(k);
                String narration = "Receipting for " + srv.getName();
                Account credit = srv.getIncomeAccount();
                BigDecimal amount = BigDecimal.valueOf(v);

                items.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, amount, BigDecimal.ZERO));
                items.add(new JournalEntryItem(credit, narration, BigDecimal.ZERO, amount));
//                items.add(new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount));
//                items.add(new JournalEntryItem(narration, credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));

            });
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
                    Account debit = srv.getExpenseAccount();//store.getInventoryAccount();// srv.getExpenseAccount();// cost of sales
                    Account credit = srv.getInventoryAssetAccount();//store.getInventoryAccount(); // Inventory Asset Account
                    BigDecimal amount = BigDecimal.valueOf(v);

                    items.add(new JournalEntryItem(debit, narration, amount, BigDecimal.ZERO));
                    items.add(new JournalEntryItem(credit, narration, BigDecimal.ZERO, amount));

//                    items.add(new JournalEntryItem(narration, debit.getIdentifier(), JournalEntryItem.Type.DEBIT, amount));
//                    items.add(new JournalEntryItem(narration, credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));
                });
            }
        }
        //
        if (!methods.isEmpty()) {
            methods.stream()
                    .forEach(method -> {
                        //check if payment method has a bank
                        if (StringUtils.isNotBlank(method.getAccountNumber())) {
                            Optional<BankAccount> bank = bankingService.findBankAccountByNumber(method.getAccountNumber());
                            if (bank.isPresent()) {
                                BankAccount account = bank.get();
//                                String bankDebitAcc = account.getLedgerAccount().getIdentifier();//debit the bank account
//                                String bankCreditAcc = debitAccount.get().getAccount().getIdentifier(); //credit receipt control

                                String narration = "Banking Patient Receipt number - " + payment.getReceiptNo();
                                BigDecimal amount = method.getAmount();
                                items.add(new JournalEntryItem(account.getLedgerAccount(), narration, amount, BigDecimal.ZERO));
                                items.add(new JournalEntryItem(debitAccount.get().getAccount(), narration, BigDecimal.ZERO, amount));

//                                items.add(new JournalEntryItem(narration, bankDebitAcc, JournalEntryItem.Type.DEBIT, amount));
//                                items.add(new JournalEntryItem(narration, bankCreditAcc, JournalEntryItem.Type.CREDIT, amount));
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
//        Optional<FinancialActivityAccount> creditors = activityAccountRepository.findByFinancialActivity(FinancialActivity.Accounts_Receivable);
//        if (!creditors.isPresent()) {
//            throw APIException.badRequest("Receipt Control Account is Not Mapped");
//        }
        List<JournalEntryItem> items = new ArrayList<>();
        if (!methods.isEmpty()) {
            methods.stream()
                    .forEach(method -> {
                        //check if payment method has a bank
                        if (StringUtils.isNotBlank(method.getAccountNumber())) {
                            Optional<BankAccount> bank = bankingService.findBankAccountByNumber(method.getAccountNumber());
                            if (bank.isPresent()) {
                                BankAccount account = bank.get();
//                                String bankDebitAcc = account.getLedgerAccount().getIdentifier();//debit the bank account
//                                String bankCreditAcc = payer.getDebitAccount().getIdentifier(); //credit receipt control

                                String narration = "Banking Remittance Number - " + method.getReference();
                                BigDecimal amount = method.getAmount();
                                items.add(new JournalEntryItem(account.getLedgerAccount(), narration, amount, BigDecimal.ZERO));
                                items.add(new JournalEntryItem(payer.getDebitAccount(), narration, BigDecimal.ZERO, amount));
//                                items.add(new JournalEntryItem(narration, bankDebitAcc, JournalEntryItem.Type.DEBIT, amount));
//                                items.add(new JournalEntryItem(narration, bankCreditAcc, JournalEntryItem.Type.CREDIT, amount));
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

}
