package io.smarthealth.debtor.claim.processing.service;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.service.InvoiceService;
import io.smarthealth.debtor.claim.processing.data.InvoiceMergeData;
import io.smarthealth.debtor.claim.processing.domain.InvoiceMerge;
import io.smarthealth.debtor.claim.processing.domain.InvoiceMergeRepository;
import io.smarthealth.debtor.payer.service.PayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceProcessService {

    private final InvoiceMergeRepository invoiceMergeRepository;
    private final InvoiceRepository invoiceRepository;
    private final PayerService payerService;
    private final InvoiceService invoiceService;

        

    @javax.transaction.Transactional
    public InvoiceMerge mergeInvoice(InvoiceMergeData data) {
        InvoiceMerge invoiceMerge = InvoiceMergeData.map(data);
        
        Invoice fromInvoice = invoiceService.findInvoiceByIdWithFailDetection(data.getFromInvoiceNumber());
        Invoice toInvoice = invoiceService.findInvoiceByIdWithFailDetection(data.getToInvoiceNumber());
        
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
    
    
    
//    public Remitance updateRemitance(final Long id, RemitanceData data) {
//        Remitance remitance = RemitanceData.map(data);
//        Payer payer = payerService.findPayerByIdWithNotFoundDetection(data.getPayerId());
//        remitance.setAmount(data.getAmount());
//        remitance.setBalance(data.getBalance());
////        remitance.setPaymentCode();
////        remitance.setReceiptNo();
//        remitance.setTransactionId("");
//        remitance.setPayer(payer);    
//        return remitanceRepository.save(remitance);
//    }
//
//    public Remitance getRemitanceByIdWithFailDetection(Long id) {
//        return remitanceRepository.findById(id).orElseThrow(() -> APIException.notFound("Remitance identified by id {0} not found ", id));
//    }
//
//    public Optional<Remitance> getRemitance(Long id) {
//        return remitanceRepository.findById(id);
//    }
//
//    public Page<Remitance> getRemitances(Long payerId, Long bankId, Double balance, DateRange range, Pageable page) {
//        Specification spec = RemitanceSpecification.createSpecification(payerId, bankId, balance, range);
//        return remitanceRepository.findAll(spec, page);
//    }
//
//    public List<Remitance> getAllRemitances() {
//        return remitanceRepository.findAll();
//    }
}
