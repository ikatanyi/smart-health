package io.smarthealth.accounting.invoice.service;


import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.CreateInvoiceData;
import io.smarthealth.accounting.invoice.data.CreateInvoiceItemData;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceMergeData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.accounting.invoice.domain.InvoiceMerge;
import io.smarthealth.accounting.invoice.domain.InvoiceMergeRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMergeRepository invoiceMergeRepository;
    private final BillingService billingService;
    private final JournalService journalService;
    private final PayerService payerService;
    private final SchemeService schemeService; 
    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
//    private final TxnService txnService;

    @Transactional
    public String createInvoice(CreateInvoiceData invoiceData) {

        String trxId = sequenceNumberService.next(1L, Sequences.Transactions.name());

//        final 
        Optional<PatientBill> bill = billingService.findByBillNumber(invoiceData.getBillNumber());
        invoiceData.getPayers()
                .stream()
                .forEach(debt -> {
                    String invoiceNo = sequenceNumberService.next(1L, Sequences.Invoice.name());

                    Payer payer = payerService.findPayerByIdWithNotFoundDetection(debt.getPayerId());
                    Scheme scheme = schemeService.fetchSchemeById(debt.getSchemeId());

                    Integer creditDays = 30;
                    String terms = "Net 30";
                    if (payer.getPaymentTerms() != null) {
                        creditDays = payer.getPaymentTerms().getCreditDays();
                        terms = payer.getPaymentTerms().getTermsName();
                    }

                    Invoice invoice = new Invoice();
                    invoice.setPayer(payer);
                    invoice.setPayee(scheme);
                    invoice.setReference(debt.getMemberNo());
                    invoice.setTransactionNo(trxId);
                    invoice.setDate(invoiceData.getDate());
                    invoice.setNotes(invoice.getNotes());

                    invoice.setDueDate(invoiceData.getDate().plusDays(creditDays));
                    invoice.setTerms(terms);
                    invoice.setNumber(invoiceNo);
                    invoice.setInvoiceNumberRequiresAutoGeneration(true);
                    invoice.setSubtotal(debt.getAmount());
                    invoice.setBalance(debt.getAmount());
                    invoice.setDisounts(invoiceData.getDiscount());
                    invoice.setTaxes(invoiceData.getTaxes());
                    invoice.setTotal(debt.getAmount());
                    invoice.setPaid(Boolean.FALSE);
                    invoice.setClosed(Boolean.FALSE);
                    invoice.setDraft(Boolean.FALSE);
                    invoice.setStatus(InvoiceStatus.draft);

                    if (bill.isPresent()) {
                        invoice.setBill(bill.get());

                    }

                    if (!invoiceData.getItems().isEmpty()) {
                        List<InvoiceLineItem> items = invoiceData.getItems()
                                .stream()
                                .map(inv -> createItem(trxId, inv))
                                .collect(Collectors.toList());

                        invoice.addItems(items);
                    }
                    saveInvoice(invoice);
                }
                );
        return trxId;

    }

    @Transactional
    public Invoice saveInvoice(Invoice invoice) {
//        Invoice savedInv = invoiceRepository.save(invoice);
//        System.err.println(savedInv.isInvoiceNumberRequiresAutoGeneration());
////        if (savedInv.isInvoiceNumberRequiresAutoGeneration()) {
//            savedInv.setNumber(sequenceGenerator.generate(savedInv));
        Invoice savedInvoice = invoiceRepository.save(invoice);
//        }
        if (savedInvoice.getBill() != null) {
            PatientBill bill = savedInvoice.getBill();
            bill.setStatus(BillStatus.Final);
            billingService.update(bill);
        }
        journalService.save(toJournal(invoice));

        return savedInvoice;
    }

    private InvoiceLineItem createItem(String trxId, CreateInvoiceItemData data) {
        InvoiceLineItem lineItem = new InvoiceLineItem();
        PatientBillItem item = billingService.findBillItemById(data.getBillItemId());
        item.setStatus(BillStatus.Final);

        lineItem.setBillItem(item);
        lineItem.setDeleted(false);

        lineItem.setTransactionId(trxId);
        return lineItem;
    }

    public Optional<Invoice> findById(final Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> findByInvoiceNumber(final String invoiceNo) {
        return invoiceRepository.findByNumber(invoiceNo);
    }

    public Page<Invoice> fetchInvoices(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range, double amountGreaterThan, boolean filterPastDue, double amountLessThanOrEqualTo, Pageable pageable) {

        Specification<Invoice> spec = InvoiceSpecification.createSpecification(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo);
        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);
        return invoices;
    }

    public Invoice updateInvoice(Long id, InvoiceData data) {
        Invoice invoice = findInvoiceOrThrowException(id);
        invoice.setBalance(data.getBalance());
        invoice.setClosed(data.getClosed());
        invoice.setCurrency(data.getCurrency());
        invoice.setDisounts(data.getDisounts());
        invoice.setDraft(data.getDraft());
        invoice.setDueDate(data.getDueDate());
        invoice.setNotes(data.getNotes());
        invoice.setPaid(data.getPaid());
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        Scheme payee = schemeService.fetchSchemeById(data.getPayeeId());
        invoice.setPayee(payee);
        invoice.setPayer(payer);
        invoice.setReference(data.getReference());
        invoice.setStatus(data.getStatus());
        invoice.setSubtotal(data.getSubtotal());
        invoice.setTaxes(data.getTaxes());
        invoice.setTerms(data.getTerms());
        invoice.setTotal(data.getTotal());
        return invoiceRepository.save(invoice);
    }

    public Invoice verifyInvoice(Long id, Boolean isVerified) {
        Invoice invoice = findInvoiceOrThrowException(id);
        invoice.setIsVerified(isVerified);
        return invoiceRepository.save(invoice);
    }

    public Invoice findByInvoiceNumberOrThrow(String invoiceNumber) {
        return invoiceRepository.findByNumber(invoiceNumber).orElseThrow(() -> APIException.notFound("Invoice with invoice number {0} not found.", invoiceNumber));
    }

    @Transactional
    public InvoiceMerge mergeInvoice(InvoiceMergeData data) {
        InvoiceMerge invoiceMerge = InvoiceMergeData.map(data);

        Invoice fromInvoice = findByInvoiceNumberOrThrow(data.getFromInvoiceNumber());
        Invoice toInvoice = findByInvoiceNumberOrThrow(data.getToInvoiceNumber());

        fromInvoice.getItems().stream().map((lineItem) -> {
            lineItem.setInvoice(toInvoice);
            return lineItem;
        }).forEachOrdered((lineItem) -> {
            toInvoice.getItems().add(lineItem);
        });

        invoiceRepository.save(toInvoice);
        invoiceRepository.deleteById(fromInvoice.getId());
        return invoiceMergeRepository.save(invoiceMerge);
    }

    public Invoice findInvoiceOrThrowException(Long id) {
        return findById(id)
                .orElseThrow(() -> APIException.notFound("Invoice with id {0} not found.", id));
    }

    public String emailInvoice(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public InvoiceData invoiceToEDI(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private JournalEntry toJournal(Invoice invoice) {

        Account debitAccount;
        if (invoice.getPayer().getDebitAccount() != null) {
            debitAccount = invoice.getPayer().getDebitAccount();
        } else {
            FinancialActivityAccount debit = activityAccountRepository
                    .findByFinancialActivity(FinancialActivity.Accounts_Receivable)
                    .orElseThrow(() -> APIException.notFound("Account Receivable Account is Not Mapped"));
            debitAccount = debit.getAccount();
        }
        FinancialActivityAccount creditAccount = activityAccountRepository
                .findByFinancialActivity(FinancialActivity.Patient_Control)
                .orElseThrow(() -> APIException.notFound("Patient Control Account is Not Mapped"));

        String creditAcc = creditAccount.getAccount().getIdentifier();
        String debitAcc = debitAccount.getIdentifier();
        BigDecimal amount = BigDecimal.valueOf(invoice.getTotal());
        String narration = "Raise Invoice - " + invoice.getNumber();
        JournalEntry toSave = new JournalEntry(invoice.getDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAccount, narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(creditAccount.getAccount(), narration,BigDecimal.ZERO, amount)
//                    new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount),
//                    new JournalEntryItem(narration, creditAcc, JournalEntryItem.Type.CREDIT, amount)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionNo());
        toSave.setTransactionType(TransactionType.Invoicing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

}
