package io.smarthealth.debtor.claim.creditNote.service;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteItemData;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteItem;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteRepository;
import io.smarthealth.debtor.claim.creditNote.domain.specification.CreditNoteSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    private final InvoiceService invoiceService;
    private final BillingService billService;
    private final PayerService payerService; 

        

    @javax.transaction.Transactional
    public CreditNote createCreditNote(CreditNoteData data) {
        CreditNote creditNote = CreditNoteData.map(data);         
        Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(data.getInvoiceNo());
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(invoice.getPayer().getId());
        creditNote.setInvoice(invoice);   
        creditNote.setPayer(payer);
        List<CreditNoteItem>creditNoteItemArr = new ArrayList();
        data.getBillItems().stream().map((item) -> {
            CreditNoteItem creditNoteItem = new CreditNoteItem();
            PatientBillItem billItem = billService.findBillItemById(item.getBillItemid());
            creditNoteItem.setAmount(billItem.getAmount());
            creditNoteItem.setBillItem(billItem);
            creditNoteItem.setItem(billItem.getItem());
            return creditNoteItem;
        }).forEachOrdered((creditNoteItem) -> {
            creditNoteItemArr.add(creditNoteItem);
        });
        creditNote.setItems(creditNoteItemArr);
        invoice.setCreditNote(creditNote);
        invoiceRepository.save(invoice);
        return creditNoteRepository.save(creditNote);
    }
    
    public CreditNote updateCreditNote(final Long id, CreditNoteData data) {
        CreditNote creditNote = getCreditNoteByIdWithFailDetection(id);
        
        Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(data.getInvoiceNo());
        creditNote.setInvoice(invoice);        
        List<CreditNoteItem>creditNoteItemArr = new ArrayList();
        data.getBillItems().stream().map((item) -> {
            CreditNoteItem creditNoteItem = new CreditNoteItem();
            PatientBillItem billItem = billService.findBillItemById(item.getBillItemid());
            creditNoteItem.setAmount(billItem.getAmount());
            creditNoteItem.setBillItem(billItem);
            creditNoteItem.setItem(billItem.getItem());
            return creditNoteItem;
        }).forEachOrdered((creditNoteItem) -> {
            creditNoteItemArr.add(creditNoteItem);
        });
        creditNote.setItems(creditNoteItemArr);
        invoice.setCreditNote(creditNote);
        invoiceRepository.save(invoice);
        return creditNoteRepository.save(creditNote);
    }

    public CreditNote getCreditNoteByIdWithFailDetection(Long id) {
        return creditNoteRepository.findById(id).orElseThrow(() -> APIException.notFound("CreditNote identified by id {0} not found ", id));
    }

    public Optional<CreditNote> getCreditNote(Long id) {
        return creditNoteRepository.findById(id);
    }

    public Page<CreditNote> getCreditNotes(String invoiceNumber, Long payerId,  DateRange range, Pageable page) {
        Specification spec = CreditNoteSpecification.createSpecification(invoiceNumber, payerId, range);
        return creditNoteRepository.findAll(spec, page);
    }

    public List<CreditNote> getAllCreditNotes() {
        return creditNoteRepository.findAll();
    }
    
    public static CreditNoteData map(CreditNote creditNote){
        CreditNoteData data = new CreditNoteData();
        data.setAmount(creditNote.getAmount());
        data.setCreditNoteNo(creditNote.getCreditNoteNo());
        creditNote.getItems().stream().map((item) -> {
            CreditNoteItemData billItem=new CreditNoteItemData();
            billItem.setAmount(item.getAmount());
            billItem.setBillItemid(item.getBillItem().getId());
            billItem.setItemId(item.getItem().getId());
            return billItem;
        }).forEachOrdered((billItem) -> {
            data.getBillItems().add(billItem);
        });
        if(creditNote.getInvoice()!=null){
            data.setInvoiceNo(creditNote.getInvoice().getNumber());
            data.setInvoiceDate(creditNote.getInvoice().getDate());
        }
        if(creditNote.getPayer()!=null){
            data.setPayer(creditNote.getPayer().getPayerName());
            data.setPayerId(creditNote.getPayer().getId());
        }
        data.setDate(LocalDate.from(creditNote.getCreatedOn()));
        data.setComments(creditNote.getComments());
        return data;
    }
}
