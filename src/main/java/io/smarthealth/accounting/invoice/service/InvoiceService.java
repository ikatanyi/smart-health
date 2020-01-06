package io.smarthealth.accounting.invoice.service;

import io.smarthealth.accounting.billing.domain.BillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.invoice.data.CreateInvoiceData;
import io.smarthealth.accounting.invoice.data.CreateInvoiceItemData;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.service.ItemService;
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

/**
 *
 * @author Kelsas
 */
@Slf4j 
@Service 
public class InvoiceService {
     private final InvoiceRepository invoiceRepository;
     private final ItemService itemService;
     private final BillingService billingService;

    public InvoiceService(InvoiceRepository invoiceRepository, ItemService itemService, BillingService billingService) {
        this.invoiceRepository = invoiceRepository;
        this.itemService = itemService;
        this.billingService = billingService;
    }

   
    @Transactional
    public InvoiceData createInvoice(CreateInvoiceData invoiceData) {
         
        final String trxId=UUID.randomUUID().toString();
         
        Invoice invoice=new Invoice(); 
         invoice.setDate(invoiceData.getDate());
         invoice.setNotes(invoice.getNotes());
         invoice.setSubtotal(invoiceData.getSubTotal());
         invoice.setDisounts(invoiceData.getDiscount());
         invoice.setTaxes(invoiceData.getTaxes());
         invoice.setTotal(invoiceData.getTotal());
          
        if(!invoiceData.getItems().isEmpty()){
                 List<InvoiceLineItem> items=  invoiceData.getItems()
                    .stream()
                    .map(inv -> createItem(trxId,inv))
                    .collect(Collectors.toList());
                 
                 invoice.addItems(items);
        }
        //
       Invoice inv=invoiceRepository.save(invoice);
       
       return InvoiceData.map(inv);
               
    }
    private InvoiceLineItem createItem(String trxId, CreateInvoiceItemData data){
         InvoiceLineItem lineItem = new InvoiceLineItem(); 
        BillItem item=billingService.findBillItemById(data.getBillItemId());
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
    public Page<Invoice> fetchInvoices(String customer,String invoice, String receipt, Pageable pageable) {
        Specification<Invoice> spec = InvoiceSpecification.createSpecification(customer, invoice);
        Page<Invoice> invoices=  invoiceRepository.findAll(spec, pageable);
        return invoices;
    }
    public InvoiceData updateInvoice(Long id, InvoiceData data){
        throw new UnsupportedOperationException("Not supported yet."); 
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
       
}
