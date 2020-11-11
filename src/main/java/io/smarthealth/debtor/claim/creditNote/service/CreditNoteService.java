package io.smarthealth.debtor.claim.creditNote.service;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.administration.servicepoint.domain.ServicePoints;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteItemData;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteItem;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteItemRepository;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteRepository;
import io.smarthealth.debtor.claim.creditNote.domain.specification.CreditNoteSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final CreditNoteItemRepository creditNoteItemRepository;
    private final InvoiceService invoiceService;
    private final BillingService billService;
    private final PayerService payerService;
    private final JournalService journalService;
    private final FinancialActivityAccountRepository activityAccountRepository;
    private final SequenceNumberService sequenceNumberService;
    private final ServicePointService servicePointService;

    @Transactional
    public CreditNote createCreditNote(CreditNoteData data) {
        CreditNote creditNote = CreditNoteData.map(data);
        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNo());
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(invoice.getPayer().getId());
        String transactionId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String creditNoteNumber = sequenceNumberService.next(1L, Sequences.CreditNoteNumber.name());
        creditNote.setCreditNoteNo(creditNoteNumber);
        creditNote.setTransactionId(transactionId);
        creditNote.setInvoice(invoice);
        creditNote.setPayer(payer);
        Double totalAmount = 0.0;
//        creditNote.setAmount(data.getAmount());
        List<CreditNoteItem> creditNoteItemArr = new ArrayList();
//        data.getBillItems().stream().map((item) -> {
        for (CreditNoteItemData item : data.getCreditNoteItems()) {
            CreditNoteItem creditNoteItem = new CreditNoteItem();
            InvoiceItem invoiceItem = invoiceService.getInvoiceItemByIdOrThrow(item.getInvoiceItemId());

            creditNoteItem.setAmount(invoiceItem.getBillItem().getAmount());
            creditNoteItem.setInvoiceItem(invoiceItem);

            creditNoteItemArr.add(creditNoteItem);

            totalAmount = totalAmount + invoiceItem.getBillItem().getAmount();
        }
//        }).forEachOrdered((creditNoteItem) -> {
//            creditNoteItemArr.add(creditNoteItem);
//        });
        creditNote.setAmount(totalAmount);
        List<CreditNoteItem> savedItems = creditNoteItemRepository.saveAll(creditNoteItemArr);
        creditNote.setItems(savedItems);
        CreditNote savedcreditNote = creditNoteRepository.save(creditNote);
//        invoice.addCreditNoteItem(savedcreditNote);
        invoiceRepository.save(invoice);

        //TODO: create journal for credit note
        journalService.save(toJournal(savedcreditNote));
        return savedcreditNote;
    }

    public CreditNote updateCreditNote(final Long id, CreditNoteData data) {
        CreditNote creditNote = getCreditNoteByIdWithFailDetection(id);

        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(data.getInvoiceNo());
        creditNote.setInvoice(invoice);
        List<CreditNoteItem> creditNoteItemArr = new ArrayList();
        data.getCreditNoteItems().stream().map((item) -> {
            CreditNoteItem creditNoteItem = new CreditNoteItem();
            InvoiceItem invoiceItem = invoiceService.getInvoiceItemByIdOrThrow(item.getInvoiceItemId());

            creditNoteItem.setAmount(invoiceItem.getBillItem().getAmount());
            creditNoteItem.setInvoiceItem(invoiceItem);

            creditNoteItemArr.add(creditNoteItem);
            return creditNoteItem;
        }).forEachOrdered((creditNoteItem) -> {
            creditNoteItemArr.add(creditNoteItem);
        });
        List<CreditNoteItem> savedItems = creditNoteItemRepository.saveAll(creditNoteItemArr);
        creditNote.setItems(savedItems);
        CreditNote savedcreditNote = creditNoteRepository.save(creditNote);
//        invoice.addCreditNoteItem(savedcreditNote);
        invoiceRepository.save(invoice);
        //TODO: create journal for credit note
        journalService.save(toJournal(savedcreditNote));
        return savedcreditNote;
    }

    public CreditNote getCreditNoteByIdWithFailDetection(Long id) {
        return creditNoteRepository.findById(id).orElseThrow(() -> APIException.notFound("CreditNote identified by id {0} not found ", id));
    }

    public CreditNote getCreditNoteByNumberWithFailDetection(String creditNoteNumber) {
        return creditNoteRepository.findByCreditNoteNo(creditNoteNumber).orElseThrow(() -> APIException.notFound("CreditNote identified by creditNoteNumber {0} not found ", creditNoteNumber));
    }

    public Optional<CreditNote> getCreditNote(Long id) {
        return creditNoteRepository.findById(id);
    }

    public Page<CreditNote> getCreditNotes(String invoiceNumber, Long payerId, DateRange range, Pageable page) {
        Specification spec = CreditNoteSpecification.createSpecification(invoiceNumber, payerId, range);
        return creditNoteRepository.findAll(spec, page);
    }

    public List<CreditNote> getAllCreditNotes() {
        return creditNoteRepository.findAll();
    }

    private JournalEntry toJournal(CreditNote creditNote) {
        List<JournalEntryItem> journalEntries = new ArrayList();
        Account creditAccount = creditNote.getPayer().getDebitAccount();
        String narration = "Credit Note " + creditNote.getCreditNoteNo() + " for invoice " + creditNote.getInvoice().getNumber();
        if (creditAccount == null) {
            throw APIException.badRequest("Payer account is Not Mapped");
        }
        BigDecimal amountToDebit = BigDecimal.ZERO;
        for (CreditNoteItem item : creditNote.getItems()) {
            ServicePoints servicepoint = servicePointService.getServicePoint(item.getInvoiceItem().getBillItem().getServicePointId());
            BigDecimal amount = BigDecimal.valueOf(item.getInvoiceItem().getBillItem().getAmount());
            amountToDebit = amountToDebit.add(amount);
            journalEntries.add(new JournalEntryItem(servicepoint.getIncomeAccount(), narration, amount, BigDecimal.ZERO));
        }
        //BigDecimal amount = BigDecimal.valueOf(creditNote.getAmount());
        journalEntries.add(new JournalEntryItem(creditAccount, narration,BigDecimal.ZERO, amountToDebit));

        JournalEntry toSave = new JournalEntry(LocalDate.now(), narration, journalEntries);
        toSave.setTransactionNo(creditNote.getTransactionId());
        toSave.setTransactionType(TransactionType.CreditNote);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
}
