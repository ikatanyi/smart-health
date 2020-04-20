package io.smarthealth.debtor.claim.writeOff.service;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.debtor.claim.writeOff.domain.specification.WriteOffSpecification;
import io.smarthealth.debtor.claim.writeOff.data.WriteOffData;
import io.smarthealth.debtor.claim.writeOff.domain.WriteOff;
import io.smarthealth.debtor.claim.writeOff.domain.WriteOffRepository;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.payer.service.PayerService;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
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
public class WriteOffService {

    private final WriteOffRepository writeOffRepository;
    private final InvoiceRepository invoiceRepository; 
    private final InvoiceService invoiceService; 
    private final PayerService payerService;
    private final SchemeService schemeService; 

        

    @Transactional
    public WriteOff createWriteOff(WriteOffData writeOffData) {
        WriteOff writeOff = WriteOffData.map(writeOffData);
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(writeOffData.getPayerId());
        Scheme scheme = schemeService.fetchSchemeById(writeOffData.getSchemeId());
        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(writeOffData.getInvoiceNo());
        writeOff.setPayer(payer);
        writeOff.setScheme(scheme);
        writeOff.setInvoice(invoice);
        invoice.setStatus(InvoiceStatus.WrittenOff);
        invoiceRepository.save(invoice);
        return writeOffRepository.save(writeOff);
    }
    
    public WriteOff updateWriteOff(final Long id, WriteOffData writeOffData) {
        WriteOff writeOff = getWriteOffByIdWithFailDetection(id);
        writeOff.setAmount(writeOffData.getAmount());
        writeOff.setComments(writeOffData.getComments());
        Payer payer = payerService.findPayerByIdWithNotFoundDetection(writeOffData.getPayerId());
        Scheme scheme = schemeService.fetchSchemeById(writeOffData.getSchemeId());
        Invoice invoice = invoiceService.getInvoiceByNumberOrThrow(writeOffData.getInvoiceNo());
        writeOff.setPayer(payer);
        writeOff.setScheme(scheme);
        writeOff.setInvoice(invoice);
        invoice.setStatus(InvoiceStatus.WrittenOff);
        invoiceRepository.save(invoice);
        return writeOffRepository.save(writeOff);
    }

    public WriteOff getWriteOffByIdWithFailDetection(Long id) {
        return writeOffRepository.findById(id).orElseThrow(() -> APIException.notFound("WriteOff identified by id {0} not found ", id));
    }

    public Optional<WriteOff> getWriteOff(Long id) {
        return writeOffRepository.findById(id);
    }

    public Page<WriteOff> getAllWriteOff(Long payerId, Long schemeId, String invoiceNo, DateRange range, Pageable page) {
        Specification spec = WriteOffSpecification.createSpecification(payerId, schemeId, invoiceNo, range);
        return writeOffRepository.findAll(spec, page);
    }

    public List<WriteOff> getAllWriteOff() {
        return writeOffRepository.findAll();
    }
    
    public static WriteOffData map(WriteOff writeOff){
        WriteOffData data = new WriteOffData();
        data.setComments(writeOff.getComments());
        data.setAmount(writeOff.getAmount());
        if(writeOff.getPayer()!=null){
            data.setPayer(writeOff.getPayer().getPayerName());
            data.setPayerId(writeOff.getPayer().getId());
        }
        if(writeOff.getScheme()!=null){
            data.setScheme(writeOff.getScheme().getSchemeName());
            data.setSchemeId(writeOff.getScheme().getId());
        }
        if(writeOff.getInvoice()!=null){
            data.setInvoiceNo(writeOff.getInvoice().getNumber());
           
        }
        return data;
    }
}
