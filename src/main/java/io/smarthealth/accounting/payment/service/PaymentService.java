package io.smarthealth.accounting.payment.service;

import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.data.FinancialTransactionData;
import io.smarthealth.accounting.payment.data.CreateTransactionData;
import io.smarthealth.accounting.payment.domain.Payment;
import io.smarthealth.accounting.payment.domain.FinancialTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrxType;
import io.smarthealth.accounting.payment.domain.specification.PaymentSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.payment.domain.FinancialTransactionRepository;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.numbers.service.SequenceNumberGenerator;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.stores.domain.Store;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final FinancialTransactionRepository transactionRepository;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final AccountService accountService;
    private final BillingService billingService;
    private final JournalService journalEntryService;

    private final SequenceNumberGenerator sequenceGenerator;

    private final SequenceNumberService sequenceNumberService;
    private final ServicePointService servicePointService;

    @Transactional
    public FinancialTransactionData createTransaction(CreateTransactionData transactionData) {
        
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receipt = sequenceNumberService.next(1L, Sequences.Receipt.name());

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDate(transactionData.getDate());
        transaction.setAmount(transactionData.getAmount());
        transaction.setTrxType(TrxType.payment);
        transaction.setReceiptNo(receipt);
        transaction.setShiftNo("0000");
        transaction.setTransactionId(trdId);
        transaction.setInvoice(transactionData.getBillNumber());
        
        if (!transactionData.getPayment().isEmpty()) {
            List<Payment> paylist = transactionData.getPayment()
                    .stream()
                    .map(p -> createPayment(p))
                    .collect(Collectors.toList());
            transaction.addPayments(paylist);
        }

        final FinancialTransaction trans = transactionRepository.save(transaction);

        Optional<PatientBill> bill = billingService.findByBillNumber(transactionData.getBillNumber());

        if (bill.isPresent()) {
            PatientBill b = bill.get();
            b.setStatus(BillStatus.Final);
            billingService.save(b);
        }

        List<PatientBillItem> billedItems = new ArrayList<>();

        if (!transactionData.getBillItems().isEmpty()) {
            transactionData.getBillItems()
                    .stream()
                    .forEach(data -> {
                        PatientBillItem item = billingService.findBillItemById(data.getBillItemId());
                        item.setPaid(Boolean.TRUE);
                        item.setStatus(BillStatus.Final);
                        item.setBalance(0D);
                        PatientBillItem i = billingService.updateBillItem(item);
                        billedItems.add(i);
                    });

        }
        journalEntryService.save(toJournal(transactionData.getDate().toLocalDate(), trdId, receipt, billedItems));
//        journalEntryService.createJournalEntry(trdId, billedItems);
        return FinancialTransactionData.map(trans);

    }

    private Payment createPayment(PaymentData data) {
        Payment pay = new Payment();
        pay.setAmount(data.getAmount());
        pay.setMethod(data.getMethod());
        pay.setCurrency(data.getCurrency());
        pay.setReferenceCode(data.getReferenceCode());
        pay.setType(data.getType());
        return pay;
    }

    public FinancialTransactionData updatePayment(final Long id, FinancialTransactionData data) {
        FinancialTransaction trans = findTransactionOrThrowException(id);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Optional<FinancialTransaction> findById(final Long id) {
        return transactionRepository.findById(id);
    }

    public Page<FinancialTransaction> fetchTransactions(String customer, String invoice, String receipt, Pageable pageable) {
        Specification<FinancialTransaction> spec = PaymentSpecification.createSpecification(customer, invoice, receipt);
        Page<FinancialTransaction> transactions = transactionRepository.findAll(spec, pageable);
        return transactions;
    }

    public String emailReceipt(Long id) {
//        Transaction trans = findTransactionOrThrowException(id);
        //TODO Implement further
//        return "Receipt send to client "+trans.getReceiptNo();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    public FinancialTransactionData refund(Long id, Double amount) {

        FinancialTransaction trans = findTransactionOrThrowException(id);
        if (amount > trans.getAmount()) {
            throw APIException.badRequest("Refund amount is more than the receipt amount");
        }

        FinancialTransaction toSave = new FinancialTransaction();
        toSave.setDate(LocalDateTime.now());
        toSave.setTrxType(TrxType.refund);
        toSave.setReceiptNo(trans.getReceiptNo());
        toSave.setShiftNo("0000");
        toSave.setTransactionId(UUID.randomUUID().toString());
        toSave.setInvoice(trans.getInvoice());
        toSave.setAccount(trans.getAccount());
        toSave.setAmount(amount);

        FinancialTransaction trns = transactionRepository.save(toSave);
        return FinancialTransactionData.map(trns);

    }

    public FinancialTransaction findTransactionOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Transaction with id {0} not found.", id));
    }

    private JournalEntry toJournal(LocalDate receiptDate, String trxId, String receipt, List<PatientBillItem> billedItems) {
        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);

        if (debitAccount.isPresent()) {
            throw APIException.badRequest("Receipt Control Account is Not Mapped");
        }
        String debitAcc = debitAccount.get().getAccount().getIdentifier();
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
                Account credit = srv.getIncomeAccount();
                BigDecimal amount = BigDecimal.valueOf(v);

                items.add(new JournalEntryItem(debitAcc, JournalEntryItem.Type.DEBIT, amount));
                items.add(new JournalEntryItem(credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));

            });
            //expenses
            Map<Long, Double> inventory = billedItems
                    .stream()
                    .filter(x -> x.getItem().isInventoryItem())
                    .collect(
                            Collectors.groupingBy(PatientBillItem::getServicePointId,
                                    Collectors.summingDouble(x -> (x.getItem().getCostRate() * x.getQuantity())))
                    );
            if (!inventory.isEmpty()) {
                inventory.forEach((k, v) -> {
                    //revenue
                    ServicePoint srv = servicePointService.getServicePoint(k);
                    Account debit = srv.getExpenseAccount();//store.getInventoryAccount();// srv.getExpenseAccount();// cost of sales
                    Account credit = srv.getInventoryAssetAccount();//store.getInventoryAccount(); // Inventory Asset Account
                    BigDecimal amount = BigDecimal.valueOf(v);
                    items.add(new JournalEntryItem(debit.getIdentifier(), JournalEntryItem.Type.DEBIT, amount));
                    items.add(new JournalEntryItem(credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));
                });
            }
        }
        String description = "Patient Receipting - " + receipt;
        JournalEntry toSave = new JournalEntry(receiptDate, description, items);
        toSave.setTransactionType(TransactionType.Receipting);
        toSave.setTransactionNo(trxId);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

}
