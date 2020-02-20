package io.smarthealth.debtor.claim.dispatch.service;

import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNoteItemRepository;
import io.smarthealth.debtor.claim.dispatch.data.DispatchData;
import io.smarthealth.debtor.claim.dispatch.data.DispatchedInvoiceData;
import io.smarthealth.debtor.claim.dispatch.domain.Dispatch;
import io.smarthealth.debtor.claim.dispatch.domain.DispatchRepository;
import io.smarthealth.debtor.claim.dispatch.domain.DispatchedInvoice;
import io.smarthealth.debtor.claim.dispatch.domain.specification.DispatchSpecification;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
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
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepository; 
    private final InvoiceService invoiceService; 
    private final PayerService payerService;
      private final SequenceNumberService sequenceNumberService; 
 
    @Transactional
    public Dispatch createDispatch(DispatchData dispatchData) {
        Dispatch dispatch = DispatchData.map(dispatchData);
        dispatch.setDispatchNo(sequenceNumberService.next(1L, Sequences.DispatchNumber.name()));
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(dispatchData.getPayerId());
        dispatch.setPayer(payer);
        List<DispatchedInvoice>dispatchInvoiceArr = new ArrayList();
        dispatchData.getDispatchInvoiceData().stream().map((item) -> {
            DispatchedInvoice dispatchInvoice = new DispatchedInvoice();
            Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(item.getInvoiceNumber());
            dispatchInvoice.setInvoice(invoice);
            return dispatchInvoice;
        }).forEachOrdered((dispatchInvoice) -> {
            dispatchInvoiceArr.add(dispatchInvoice);
        });
        dispatch.setDispatchedInvoice(dispatchInvoiceArr);
        return dispatchRepository.save(dispatch);
    }
    
    public Dispatch updateDispatch(final Long id, DispatchData dispatchData) {
        Dispatch dispatch = getDispatchByIdWithFailDetection(id);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(dispatchData.getPayerId());
        dispatch.setPayer(payer);
        List<DispatchedInvoice>dispatchInvoiceArr = new ArrayList();
        dispatchData.getDispatchInvoiceData().stream().map((item) -> {
            DispatchedInvoice dispatchInvoice = new DispatchedInvoice();
            Invoice invoice = invoiceService.findByInvoiceNumberOrThrow(item.getInvoiceNumber());
            dispatchInvoice.setInvoice(invoice);
            return dispatchInvoice;
        }).forEachOrdered((dispatchInvoice) -> {
            dispatchInvoiceArr.add(dispatchInvoice);
        });
        dispatch.setDispatchedInvoice(dispatchInvoiceArr);
        return dispatchRepository.save(dispatch);
    }

    public Dispatch getDispatchByIdWithFailDetection(Long id) {
        return dispatchRepository.findById(id).orElseThrow(() -> APIException.notFound("Dispatch identified by id {0} not found ", id));
    }

    public Optional<Dispatch> getDispatch(Long id) {
        return dispatchRepository.findById(id);
    }

    public Page<Dispatch> getAllDispatches(Long payerId,  DateRange range, Pageable page) {
        Specification spec = DispatchSpecification.createSpecification(payerId, range);
        return dispatchRepository.findAll(spec, page);
    }

    public List<Dispatch> getAllDispatchesNotes() {
        return dispatchRepository.findAll();
    }
    
    public static DispatchData map(Dispatch dispatch){
        DispatchData data = new DispatchData();
        data.setDispatchNo(dispatch.getDispatchNo());
        data.setComments(dispatch.getComments());
        dispatch.getDispatchedInvoice().stream().map((invoice) -> {
            DispatchedInvoiceData dispInvoice=new DispatchedInvoiceData();
            dispInvoice.setInvoiceNumber(invoice.getInvoice().getNumber());
            return dispInvoice;
        }).forEachOrdered((dispInvoice) -> {
            data.getDispatchInvoiceData().add(dispInvoice);
        });
        if(dispatch.getPayer()!=null){
            data.setPayerId(dispatch.getPayer().getId());
            data.setPayer(dispatch.getPayer().getPayerName());
        }
        data.setDispatchDate(LocalDate.from(dispatch.getCreatedOn()));
        return data;
    }
}
