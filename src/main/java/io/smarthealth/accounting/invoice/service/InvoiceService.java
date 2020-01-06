package io.smarthealth.accounting.invoice.service;

import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.LineItemData;
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.invoice.domain.InvoiceRepository;
import io.smarthealth.accounting.invoice.domain.LineItem;
import io.smarthealth.accounting.invoice.domain.specification.InvoiceSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.List;
import java.util.Optional; 
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

    public InvoiceService(InvoiceRepository invoiceRepository, ItemService itemService) {
        this.invoiceRepository = invoiceRepository;
        this.itemService = itemService;
    }
 

    @Transactional
    public InvoiceData createInvoice(InvoiceData invoiceData) {
         Invoice data=new Invoice();
        data.setPayer(invoiceData.getPayer());
        data.setNumber(invoiceData.getNumber());
        data.setName(invoiceData.getName());
        data.setCurrency(invoiceData.getCurrency());
        data.setDraft(invoiceData.getDraft());
        data.setClosed(invoiceData.getClosed());
        data.setPaid(invoiceData.getPaid());
        data.setStatus(invoiceData.getStatus());
        data.setDate(invoiceData.getDate());
        data.setDueDate(invoiceData.getDueDate());
        data.setPaymentTerms(invoiceData.getPaymentTerms());
        data.setNotes(invoiceData.getNotes());
        data.setSubtotal(invoiceData.getSubtotal());
        data.setTotal(invoiceData.getTotal());
        data.setBalance(invoiceData.getBalance());
        
        if(!invoiceData.getItems().isEmpty()){
                 List<LineItem> items=  invoiceData.getItems()
                    .stream()
                    .map(inv -> createItem(inv))
                    .collect(Collectors.toList());
                 
                 data.addItems(items);
        }
       Invoice inv=invoiceRepository.save(data);
       return InvoiceData.map(inv);
               
    }
    private LineItem createItem(LineItemData lineItemData){
         LineItem lineItem = new LineItem();
        lineItem.setId(lineItemData.getId());
        if (lineItemData.getItem() != null) {
            Item item=itemService.findItemEntityOrThrow(lineItemData.getId());
            lineItem.setItem(item);
        }
        lineItem.setType(lineItemData.getType());
        lineItem.setDescription(lineItemData.getDescription());
        lineItem.setQuantity(lineItemData.getQuantity());
        lineItem.setUnitCost(lineItemData.getUnitCost());
        lineItem.setAmount(lineItemData.getAmount());
        
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
