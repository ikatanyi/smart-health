package io.smarthealth.accounting.old.service;

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
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorInvoiceRepository;
import io.smarthealth.accounting.old.data.PaymentoldData;
import io.smarthealth.accounting.old.data.FinancialTransactionData;
import io.smarthealth.accounting.old.data.CreatePayment;
import io.smarthealth.accounting.payment.data.MakePayment;
import io.smarthealth.accounting.old.domain.Paymentold;
import io.smarthealth.accounting.old.domain.FinancialTransaction;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
//import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.old.domain.FinancialTransactionRepository;
import io.smarthealth.accounting.old.domain.specification.PaymentOldSpecification;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.organization.bank.service.BankAccountService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.PurchaseInvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class PaymentOldService {

    private final FinancialTransactionRepository transactionRepository;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final AccountService accountService;
    private final BillingService billingService;
    private final JournalService journalEntryService;

    private final SequenceNumberService sequenceNumberService;
    private final ServicePointService servicePointService;
    private final BankAccountService bankAccountService;
//    private final InvoiceRepository invoiceRepository;
    private final DoctorInvoiceRepository doctorInvoiceRepository;
    private final PurchaseInvoiceRepository purchaseInvoiceRepository;

    @Transactional
    public FinancialTransaction createTransaction(CreatePayment transactionData) {

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receipt = sequenceNumberService.next(1L, Sequences.Receipt.name());

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(transactionData.getAmount());
        transaction.setTrxType(TrnxType.Payment);
        transaction.setReceiptNo(receipt);
        transaction.setShiftNo("0000");
        transaction.setTransactionId(trdId);
        transaction.setInvoice(transactionData.getBillNumber());

        if (!transactionData.getPayment().isEmpty()) {
            List<Paymentold> paylist = transactionData.getPayment()
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
                        //update the bill as 
                    });

        }
        journalEntryService.save(toJournal(transactionData.getDate().toLocalDate(), trdId, receipt, billedItems));
//        journalEntryService.createJournalEntry(trdId, billedItems);
        return trans;

    }

    @Transactional
    public FinancialTransaction createTransaction(MakePayment creditorData) {
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String receipt = sequenceNumberService.next(1L, Sequences.Receipt.name());

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(creditorData.getAmount().doubleValue());
        transaction.setTrxType(TrnxType.Payment);
        transaction.setReceiptNo(receipt);
        transaction.setShiftNo("0000");
        transaction.setTransactionId(trdId);
        transaction.setInvoice("reference invoices paid");

        Paymentold pay = new Paymentold();
        pay.setAmount(creditorData.getAmount().doubleValue());
        pay.setMethod(creditorData.getPaymentMethod());
        pay.setCurrency(creditorData.getPaymentMethod());
        pay.setReferenceCode(receipt);

        transaction.addPayment(pay);

        creditorData.getInvoices()
                .stream()
                .forEach(x -> {
                    if (creditorData.getCreditorType().equals("Doctors")) {
                        updateDoctorInvoice(creditorData.getCreditorId(), x.getInvoiceNo(), x.getAmountPaid());
                    } else {
                        updateInvoiceBalance(creditorData.getCreditorId(),x.getInvoiceNo(), x.getAmountPaid());
                    }
                });

        //TODO: allocate the invoices paid - mark
        //find the bank and journal this transactions
        //'allocate this invoices
        final FinancialTransaction trans = transactionRepository.save(transaction);
        return trans;
    }

    private Paymentold createPayment(PaymentoldData data) {
        Paymentold pay = new Paymentold();
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
        Specification<FinancialTransaction> spec = PaymentOldSpecification.createSpecification(customer, invoice, receipt);
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
    public FinancialTransaction refund(Long id, Double amount) {

        FinancialTransaction trans = findTransactionOrThrowException(id);
        if (amount > trans.getAmount()) {
            throw APIException.badRequest("Refund amount is more than the receipt amount");
        }
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        FinancialTransaction toSave = new FinancialTransaction();
        toSave.setDate(LocalDateTime.now());
        toSave.setTrxType(TrnxType.Refund);
        toSave.setReceiptNo(trans.getReceiptNo());
        toSave.setShiftNo("0000");
        toSave.setTransactionId(trdId);
        toSave.setInvoice(trans.getInvoice());
        toSave.setAccount(trans.getAccount());
        toSave.setAmount(amount);

        FinancialTransaction trns = transactionRepository.save(toSave);
        return trns;

    }

    private void updateInvoiceBalance(Long supplierId,String invoiceNo, BigDecimal amountPaid) {
        Optional<PurchaseInvoice> invoice = purchaseInvoiceRepository.findByInvoiceForSupplier(invoiceNo, supplierId);
        if (invoice.isPresent()) {
            PurchaseInvoice inv = invoice.get();
            BigDecimal newBal =inv.getInvoiceBalance().subtract(amountPaid);
            boolean paid = newBal.doubleValue() <= 0;
            inv.setInvoiceBalance(newBal);
            inv.setPaid(paid);
            purchaseInvoiceRepository.save(inv);
        }
    }

    private void updateDoctorInvoice(Long doctorId, String invoiceNo, BigDecimal amountPaid) {
        Optional<DoctorInvoice> invoice = doctorInvoiceRepository.findByInvoiceForDoctor(invoiceNo, doctorId);
        if (invoice.isPresent()) {
            DoctorInvoice inv = invoice.get();
            BigDecimal newBal = inv.getBalance().subtract(amountPaid);
            boolean paid = newBal.doubleValue() <= 0;
            inv.setBalance(newBal);
            inv.setPaid(paid);
            doctorInvoiceRepository.save(inv);
        }
    }

    public FinancialTransaction findTransactionOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Transaction with id {0} not found.", id));
    }

    private JournalEntry toJournal(LocalDate receiptDate, String trxId, String receipt, List<PatientBillItem> billedItems) {
        Optional<FinancialActivityAccount> debitAccount = activityAccountRepository.findByFinancialActivity(FinancialActivity.Receipt_Control);

        if (!debitAccount.isPresent()) {
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
                String narration = "Receipting for " + srv.getName();
                Account credit = srv.getIncomeAccount();
                BigDecimal amount = BigDecimal.valueOf(v);

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
//                    items.add(new JournalEntryItem(narration, debit.getIdentifier(), JournalEntryItem.Type.DEBIT, amount));
//                    items.add(new JournalEntryItem(narration, credit.getIdentifier(), JournalEntryItem.Type.CREDIT, amount));
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
