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
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.CreateInvoice;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.data.InvoiceMergeData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.accounting.invoice.domain.InvoiceItemRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceMerge;
import io.smarthealth.accounting.invoice.domain.InvoiceMergeRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceMergeRepository invoiceMergeRepository;
    private final BillingService billingService;
    private final JournalService journalService;
    private final PayerService payerService;
    private final SchemeService schemeService;
    private final SequenceNumberService sequenceNumberService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final VisitService visitService;
//    private final TxnService txnService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Invoice> createInvoice(CreateInvoice invoiceData) {

        Visit visit = visitService.findVisitEntityOrThrow(invoiceData.getVisitNumber());

        String trxId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        List<Invoice> savedInvoices = new ArrayList<>();

        invoiceData.getPayers()
                .stream()
                .forEach(
                        payerData -> {
                            String invoiceNo = sequenceNumberService.next(1L, Sequences.Invoice.name());

                            Payer payer = payerService.findPayerByIdWithNotFoundDetection(payerData.getPayerId());
                            Scheme scheme = schemeService.fetchSchemeById(payerData.getSchemeId());

                            Integer creditDays = 30;
                            String terms = "Net 30";
                            if (payer.getPaymentTerms() != null) {
                                creditDays = payer.getPaymentTerms().getCreditDays();
                                terms = payer.getPaymentTerms().getTermsName();
                            }

                            Invoice invoice = new Invoice();
                            invoice.setAmount(payerData.getAmount());
                            invoice.setBalance(payerData.getAmount());
                            invoice.setDate(invoiceData.getDate());
                            invoice.setDueDate(invoiceData.getDate().plusDays(creditDays));

                            invoice.setDiscount(invoiceData.getDiscount());
                            invoice.setMemberName(payerData.getMemberName());
                            invoice.setMemberNumber(payerData.getMemberNo());
                            invoice.setNotes(invoiceData.getNotes());
                            invoice.setNumber(invoiceNo);
                            invoice.setPaid(Boolean.FALSE);
                            invoice.setPatient(visit.getPatient());
                            invoice.setPayer(payer);
                            invoice.setTerms(terms);
                            invoice.setScheme(scheme);
                            invoice.setStatus(InvoiceStatus.Draft);
                            invoice.setTax(invoiceData.getTaxes());
                            invoice.setTransactionNo(trxId);
                            invoice.setVisit(visit);

                            if (!invoiceData.getItems().isEmpty()) {
                                BigDecimal balance = BigDecimal.ZERO;
                                invoiceData.getItems()
                                        .stream()
                                        .forEach(inv -> {

                                            InvoiceItem lineItem = new InvoiceItem();
                                            PatientBillItem item = billingService.findBillItemById(inv.getBillItemId());
                                            BigDecimal bal = BigDecimal.valueOf(item.getAmount()).subtract(inv.getAmount());
                                            item.setPaid(Boolean.TRUE);
                                            item.setStatus(BillStatus.Paid);
                                            item.setPaymentReference(invoiceNo);
                                            item.setBalance(0D);
                                            PatientBillItem updatedItem = billingService.updateBillItem(item);
                                            lineItem.setBillItem(updatedItem);

//                                            lineItem.setBalance(inv.getAmount().doubleValue() > 0 ? inv.getAmount() : BigDecimal.ZERO);
                                            lineItem.setBalance(inv.getAmount());
                                            invoice.addItem(lineItem);
                                        });

                                invoice.setBalance(invoiceData.getItems()
                                        .stream()
                                        .map(x -> x.getAmount())
                                        .reduce(BigDecimal.ZERO, (x, y) -> x.add(y))
                                );
                            }

                            savedInvoices.add(saveInvoice(invoice));
                        }
                );

        return savedInvoices;

    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByNumber(String number) {
        return invoiceRepository.findByNumber(number);
    }

    //
    public Invoice getInvoiceByIdOrThrow(Long id) {
        return getInvoiceById(id)
                .orElseThrow(() -> APIException.notFound("Invoice with ID {0} not found.", id));
    }

    public Invoice getInvoiceByNumberOrThrow(String number) {
        return getInvoiceByNumber(number)
                .orElseThrow(() -> APIException.notFound("Invoice with Number  {0} not found.", number));
    }

    public Invoice saveInvoice(Invoice invoice) {
        Invoice savedInvoice = invoiceRepository.save(invoice);
        journalService.save(toJournal(invoice));
        return savedInvoice;
    }

    public void updateInvoiceStatus(Long id, InvoiceStatus status) {
        getInvoiceByIdOrThrow(id);
        invoiceRepository.updateInvoiceStatus(status, id);
    }

    public Invoice updateInvoice(Invoice invoice) {
        if (invoice != null) {
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    public Page<Invoice> fetchInvoices(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range, Double amountGreaterThan, Boolean filterPastDue, Double amountLessThanOrEqualTo, Pageable pageable) {
      
        Specification<Invoice> spec = InvoiceSpecification.createSpecification(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, amountLessThanOrEqualTo);
        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);
        return invoices;
    }
    
    public InvoiceItem getInvoiceItemByIdOrThrow(Long id) {
        return invoiceItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("InvoiceItem with ID {0} not found.", id));
    }


//    public Invoice updateInvoice(Long id, InvoiceData data) {
//        Invoice invoice = findInvoiceOrThrowException(id);
//        Payer payer = payerService.findPayerByIdWithNotFoundDetection(payerData.getPayerId());
//        Scheme scheme = schemeService.fetchSchemeById(payerData.getSchemeId());
//
//        invoice.setAmount(payerData.getAmount());
//        invoice.setBalance(payerData.getAmount());
//        invoice.setDate(invoiceData.getDate());
//        invoice.setBalance(payerData.getAmount());
//        invoice.setDiscount(invoiceData.getDiscount());
//        invoice.setMemberName(payerData.getMemberName());
//        invoice.setMemberNumber(payerData.getMemberNo());
//        invoice.setNotes(invoiceData.getNotes());
//        invoice.setNumber(invoiceNo);
//        invoice.setPaid(Boolean.FALSE);
//        invoice.setPatient(visit.getPatient());
//        invoice.setPayer(payer);
//        invoice.setTerms(terms);
//        invoice.setScheme(scheme);
//        invoice.setStatus(InvoiceStatus.Draft);
//        invoice.setTax(invoiceData.getTaxes());
//        invoice.setTransactionNo(trxId);
//        invoice.setVisit(visit);
//        return invoiceRepository.save(invoice);
//    }
//    public Invoice verifyInvoice(Long id, Boolean isVerified) {
//        Invoice invoice = findInvoiceOrThrowException(id);
//        invoice.setIsVerified(isVerified);
//        return invoiceRepository.save(invoice);
//    }
//    public Invoice findByInvoiceNumberOrThrow(String invoiceNumber) {
//        return invoiceRepository.findByNumber(invoiceNumber).orElseThrow(() -> APIException.notFound("Invoice with invoice number {0} not found.", invoiceNumber));
//    }
    @Transactional
    public InvoiceMerge mergeInvoice(InvoiceMergeData data) {
        InvoiceMerge invoiceMerge = InvoiceMergeData.map(data);

        Invoice fromInvoice = getInvoiceByNumberOrThrow(data.getFromInvoiceNumber());
        Invoice toInvoice = getInvoiceByNumberOrThrow(data.getToInvoiceNumber());

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
    public Invoice cancelInvoice(String invoiceNo, List<InvoiceItemData> items){
        //TODO check if invoice exists
        
        //if items provided cancel the said items
        
        //otherwise cancel the entire
        
        // post the journal
        return new Invoice();
    }

//    public Invoice findInvoiceOrThrowException(Long id) {
//        return findById(id)
//                .orElseThrow(() -> APIException.notFound("Invoice with id {0} not found.", id));
//    }
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

//        String creditAcc = creditAccount.getAccount().getIdentifier();
//        String debitAcc = debitAccount.getIdentifier();
        BigDecimal amount = invoice.getAmount();
        String narration = "Raise Invoice - " + invoice.getNumber();
        JournalEntry toSave = new JournalEntry(invoice.getDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAccount, narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(creditAccount.getAccount(), narration, BigDecimal.ZERO, amount)
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
