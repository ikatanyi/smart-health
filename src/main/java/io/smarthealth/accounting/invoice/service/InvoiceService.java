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
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.CreateInvoice;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceEditData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.data.InvoiceMergeData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.accounting.invoice.domain.InvoiceItemRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceMerge;
import io.smarthealth.accounting.invoice.domain.InvoiceMergeRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceItemSpecification;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final PaymentDetailsService paymentDetailsService;
//    private final TxnService txnService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Invoice> createInvoice(CreateInvoice invoiceData) {

        Visit visit = visitService.findVisitEntityOrThrow(invoiceData.getVisitNumber());
        Optional<PaymentDetails> paymentDetails = paymentDetailsService.getPaymentDetailsByVist(visit);

        String trxId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        List<Invoice> savedInvoices = new ArrayList<>();

        invoiceData.getPayers()
                .stream()
                .forEach(payerData -> {
                    String invoiceNo = sequenceNumberService.next(1L, Sequences.Invoice.name());

                    Payer payer = payerService.findPayerByIdWithNotFoundDetection(payerData.getPayerId());
                    Scheme scheme = schemeService.fetchSchemeById(payerData.getSchemeId());

                    Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);

                    Integer creditDays = 30;
                    String terms = "Net 30";
                    if (payer.getPaymentTerms() != null) {
                        creditDays = payer.getPaymentTerms().getCreditDays();
                        terms = payer.getPaymentTerms().getTermsName();
                    }
                    //determining the invoice amount to use 
                    BigDecimal invoiceAmount = payerData.getAmount();
                    BigDecimal discount = invoiceData.getDiscount();
                    Boolean isCapitation = Boolean.FALSE;
                    if (config.isPresent() && config.get().isCapitationEnabled()) {
                        //if this is a capitation invoice we going to mess this shit up
                        invoiceAmount = config.get().getCapitationAmount();
                        discount = BigDecimal.ZERO;
                        isCapitation = Boolean.TRUE;
                    }

                    Invoice invoice = new Invoice();
                    invoice.setAmount(invoiceAmount);
                    invoice.setBalance(invoiceAmount);
                    invoice.setCapitation(isCapitation);
                    invoice.setDate(invoiceData.getDate());
                    invoice.setDueDate(invoiceData.getDate().plusDays(creditDays));
                    invoice.setInvoiceAmount(payerData.getAmount());
                    invoice.setDiscount(discount);
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
                    if (paymentDetails.isPresent()) {
                        invoice.setIdNumber(paymentDetails.get().getIdNo());
                    }

                    if (config.isPresent()) {
                        if (config.get().isSmartEnabled()) {
                            invoice.setAwaitingSmart(Boolean.TRUE);
                        }
                    }

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
                        if (config.isPresent() && config.get().isCapitationEnabled()) {
                            invoice.setBalance(invoiceAmount);
                        } else {
                            invoice.setBalance(invoiceData.getItems()
                                    .stream()
                                    .map(x -> x.getAmount())
                                    .reduce(BigDecimal.ZERO, (x, y) -> x.add(y))
                            );
                        }
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

    public void updateInvoiceSmartStatus(Long id, Boolean awaitingSmart) {
        Invoice invoice = getInvoiceByIdOrThrow(id);
        invoice.setAwaitingSmart(awaitingSmart);
        invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Invoice invoice) {
        if (invoice != null) {
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    public Page<Invoice> fetchInvoices(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range, Double amountGreaterThan, Boolean filterPastDue, Boolean awaitingSmart, Double amountLessThanOrEqualTo, Boolean hasCapitation, Pageable pageable) {

        Specification<Invoice> spec = InvoiceSpecification.createSpecification(payer, scheme, invoice, status, patientNo, range, amountGreaterThan, filterPastDue, awaitingSmart, amountLessThanOrEqualTo, hasCapitation);
        Page<Invoice> invoices = invoiceRepository.findAll(spec, pageable);
//        Page<Invoice> invoices = invoiceRepository.findByItemsVoidedFalse(spec, pageable);

        return invoices;
    }

    public Page<InvoiceItem> fetchVoidedInvoiceItem(Long payer, Long scheme, String invoice, InvoiceStatus status, String patientNo, DateRange range, Pageable page) {

        Specification<InvoiceItem> spec = InvoiceItemSpecification.createSpecification(payer, scheme, invoice, status, patientNo, range);
        Page<InvoiceItem> invoices = invoiceItemRepository.findAll(spec, page);

        return invoices;
    }

    public InvoiceItem getInvoiceItemByIdOrThrow(Long id) {
        return invoiceItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("InvoiceItem with ID {0} not found.", id));
    }

    public Invoice updateInvoice(Long id, InvoiceEditData data) {
        Invoice invoice = getInvoiceByIdOrThrow(id);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
        Scheme scheme = schemeService.fetchSchemeById(data.getSchemeId());
        

        Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);

        //determining the invoice amount to use 
       
        invoice.setMemberNumber(data.getMemberNumber());
        invoice.setMemberName(data.getMemberName());         
        invoice.setNotes(data.getNotes());
        invoice.setPayer(payer);
        invoice.setScheme(scheme);
        return invoiceRepository.save(invoice);
    }


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

    //TODO cancelling of the invoice
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Invoice cancelInvoice(String invoiceNo, List<InvoiceItemData> items) {
        Invoice invoice = invoiceRepository.findByNumber(invoiceNo).orElseThrow(() -> APIException.notFound("Invoice Number {0} not found", invoiceNo));

        BigDecimal oldAmount = invoice.getBalance();

        List<InvoiceItem> lists
                = items.stream()
                        .map(x -> {
//                            PatientBillItem item = billingService.findBillItemById(x.getItemId());
                            Optional<InvoiceItem> invoiceItem = invoiceItemRepository.findById(x.getId());
                            if (invoiceItem.isPresent()) {
                                InvoiceItem iv = invoiceItem.get();
                                iv.setVoided(Boolean.TRUE);
                                iv.setRemarks(x.getRemarks());
                                iv.setVoidedBy(SecurityUtils.getCurrentUserLogin().orElse("system"));
                                iv.setVoidedDatetime(LocalDateTime.now());
                                BigDecimal newAmt = invoice.getAmount().subtract(iv.getBalance());
                                BigDecimal bal = invoice.getBalance().subtract(iv.getBalance());
                                invoice.setBalance(bal);
                                invoice.setAmount(newAmt);
                                return iv;
                            }
                            return null;
                        })
                        .filter(bill -> bill != null)
                        .collect(Collectors.toList());

        invoiceItemRepository.saveAll(lists);
        //if items provided cancel the said items
        //otherwise cancel the entire
        BigDecimal amount = lists.stream().map(x -> x.getBalance()).reduce(BigDecimal.ZERO, (x, y) -> x.add(y));
        BigDecimal adjustedAmount = oldAmount.subtract(amount);
        reverseInvoiceItem(invoice, adjustedAmount);

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
        BigDecimal debitAmount = amount;
        BigDecimal creditAmount = amount;
        Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(invoice.getScheme());
        List<JournalEntryItem> capitationJournal = new ArrayList<>();
        if (config.isPresent() && config.get().isCapitationEnabled()) {
            BigDecimal capitationAmount = config.get().getCapitationAmount();
            //capitations export the       3800-7000
            BigDecimal capitationDiff = (capitationAmount.subtract(invoice.getInvoiceAmount()));
            switch (capitationDiff.signum()) {
                case -1: {
                    //negative - expense
                    FinancialActivityAccount capitationAccount = activityAccountRepository
                            .findByFinancialActivity(FinancialActivity.CapitationExpense)
                            .orElseThrow(() -> APIException.notFound("Capitation Expense Account is Not Mapped"));
                    //deferrence is what I post here otherwise 
                    debitAmount = capitationAmount;
                    creditAmount = invoice.getInvoiceAmount();

                    JournalEntryItem capitationExp = new JournalEntryItem(capitationAccount.getAccount(), "Capitation Expense for Invoice No. " + invoice.getNumber(), (capitationDiff.negate()), BigDecimal.ZERO);
                    capitationJournal.add(capitationExp);
                }
                break;
                case 1: {
                    FinancialActivityAccount capitationAccount = activityAccountRepository
                            .findByFinancialActivity(FinancialActivity.CapitationIncome)
                            .orElseThrow(() -> APIException.notFound("Capitation Income Account is Not Mapped"));
                    //deferrence is what I post here otherwise 
                    debitAmount = capitationAmount;
                    creditAmount = invoice.getInvoiceAmount();

                    JournalEntryItem capitationIncomel = new JournalEntryItem(capitationAccount.getAccount(), "Capitation Income  for Invoice No. " + invoice.getNumber(), BigDecimal.ZERO, capitationDiff);
                    capitationJournal.add(capitationIncomel);
                }
                break;
                default:
            }

        }
        String narration = "Raise Invoice - " + invoice.getNumber();
        capitationJournal.add(new JournalEntryItem(debitAccount, narration, debitAmount, BigDecimal.ZERO));
        capitationJournal.add(new JournalEntryItem(creditAccount.getAccount(), narration, BigDecimal.ZERO, creditAmount));

        for (JournalEntryItem d : capitationJournal) {
            System.err.println("Account : " + d.getAccount().getName() + " Debit: " + d.getDebit() + " Credit: " + d.getCredit() + " ISDebit: " + d.isDebit());
            System.err.println("Cred");
        }
        JournalEntry toSave = new JournalEntry(invoice.getDate(), narration, capitationJournal
        //                new JournalEntryItem[]{
        //                    new JournalEntryItem(debitAccount, narration, debitAmount, BigDecimal.ZERO),
        //                    new JournalEntryItem(creditAccount.getAccount(), narration, BigDecimal.ZERO, creditAmount)
        ////                    new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount),
        ////                    new JournalEntryItem(narration, creditAcc, JournalEntryItem.Type.CREDIT, amount)
        //                }
        );

        toSave.setTransactionNo(invoice.getTransactionNo());
        toSave.setTransactionType(TransactionType.Invoicing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    private JournalEntry reverseInvoiceItem(Invoice invoice, BigDecimal amount) {

        Account reverseCredit;
        if (invoice.getPayer().getDebitAccount() != null) {
            reverseCredit = invoice.getPayer().getDebitAccount();
        } else {
            FinancialActivityAccount debit = activityAccountRepository
                    .findByFinancialActivity(FinancialActivity.Accounts_Receivable)
                    .orElseThrow(() -> APIException.notFound("Account Receivable Account is Not Mapped"));
            reverseCredit = debit.getAccount();
        }
        FinancialActivityAccount reverseDebit = activityAccountRepository
                .findByFinancialActivity(FinancialActivity.Patient_Control)
                .orElseThrow(() -> APIException.notFound("Patient Control Account is Not Mapped"));

        String narration = "Invoice Bill Reversal - " + invoice.getNumber();
        JournalEntry toSave = new JournalEntry(invoice.getDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(reverseDebit.getAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(reverseCredit, narration, BigDecimal.ZERO, amount)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionNo());
        toSave.setTransactionType(TransactionType.Bill_Reversal);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    @Transactional
    public Invoice addInvoiceItem(String invoiceNumber, List<BillItemData> invoiceItems) {
        Invoice invoice = getInvoiceByNumberOrThrow(invoiceNumber);
        // create the patient bill and then invoice them
        Double amount = invoiceItems.stream()
                .map(x -> x.getAmount())
                .reduce(0D, (x, y) -> x + y);
//
//        PatientBill patientbill = new PatientBill();
//        patientbill.setVisit(invoice.getVisit());
//        patientbill.setPatient(invoice.getPatient());
//        patientbill.setWalkinFlag(Boolean.FALSE);
//
//        patientbill.setAmount(amount);
//        patientbill.setDiscount(0D);
//        patientbill.setBalance(0D);
//        patientbill.setBillingDate(invoice.getDate());
//        patientbill.setPaymentMode(invoice.getVisit().getPaymentMethod().name());
//
//        patientbill.setStatus(BillStatus.Paid);
//
//        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
//        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
//
//        patientbill.setBillNumber(bill_no);
//        patientbill.setTransactionId(trdId);
//
//        List<PatientBillItem> lineItems = invoiceItems
//                .stream()
//                .map(lineData -> {
//                    Item item = itemRepository.findById(lineData.getItemId()).orElseThrow(() -> APIException.notFound("Item with ID {0} Not Found.", lineData.getItemId()));
//                    PatientBillItem billItem = new PatientBillItem();
//
//                    billItem.setBillingDate(lineData.getBillingDate());
//                    billItem.setTransactionId(trdId);
//                    billItem.setItem(item);
//                    billItem.setPaid(Boolean.TRUE);
//                    billItem.setPrice(lineData.getPrice());
//                    if (item.getCategory().equals(ItemCategory.CoPay)) {
//                        billItem.setPrice(lineData.getAmount());
//                    }
//                    billItem.setQuantity(lineData.getQuantity());
//                    billItem.setAmount(lineData.getAmount());
//                    billItem.setDiscount(lineData.getDiscount());
//                    billItem.setBalance(lineData.getAmount());
//                    billItem.setServicePoint(lineData.getServicePoint());
//                    billItem.setServicePointId(lineData.getServicePointId());
//                    billItem.setStatus(BillStatus.Draft);
//                    billItem.setMedicId(lineData.getMedicId());
//                    return billItem;
//                })
//                .collect(Collectors.toList());
//        patientbill.addBillItems(lineItems);

        BillData billdata = new BillData();
        billdata.setVisitNumber(invoice.getVisit().getVisitNumber());
        billdata.setBillingDate(invoice.getDate());
        billdata.setPaymentMode("Insurance");
        billdata.setWalkinFlag(Boolean.FALSE);
        billdata.setBillItems(invoiceItems);
        billdata.setAmount(amount);

        PatientBill patientBill = billingService.createPatientBill(billdata);
        //then finalize this bill
        patientBill.getBillItems()
                .stream()
                .forEach(item -> {

                    InvoiceItem lineItem = new InvoiceItem();
                    item.setPaid(Boolean.TRUE);
                    item.setStatus(BillStatus.Paid);
                    item.setPaymentReference(invoice.getNumber());
                    item.setBalance(0D);
                    PatientBillItem updatedItem = billingService.updateBillItem(item);
                    lineItem.setBillItem(updatedItem);
//                lineItem.setBalance(inv.getAmount().doubleValue() > 0 ? inv.getAmount() : BigDecimal.ZERO);
                    lineItem.setBalance(BigDecimal.valueOf(item.getAmount()));
                    invoice.addItem(lineItem);
                });

        BigDecimal addBal = patientBill.getBillItems()
                .stream().map(x -> BigDecimal.valueOf(0))
                .reduce(BigDecimal.ZERO, (x, y) -> x.add(y));

        invoice.setBalance(invoice.getBalance().add(addBal));
        //recalculate the new balance
        Invoice savedInvoice = invoiceRepository.save(invoice);
        //to journal the amendments
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

        BigDecimal amt = BigDecimal.valueOf(amount);

        String narration = "Amended Invoice  - " + invoice.getNumber();
        JournalEntry toSave = new JournalEntry(invoice.getDate(), narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(debitAccount, narration, amt, BigDecimal.ZERO),
                    new JournalEntryItem(creditAccount.getAccount(), narration, BigDecimal.ZERO, amt)
                }
        );
        toSave.setTransactionNo(invoice.getTransactionNo());
        toSave.setTransactionType(TransactionType.Invoicing);
        toSave.setStatus(JournalState.PENDING);

        journalService.save(toSave);
        return savedInvoice;
    }
}
