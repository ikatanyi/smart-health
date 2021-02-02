package io.smarthealth.accounting.invoice.service;
   
import io.smarthealth.accounting.invoice.domain.MiscInvoiceRepository;
import io.smarthealth.accounting.invoice.domain.MiscellaneousInvoice;
import io.smarthealth.accounting.invoice.data.MiscellaneousInvoiceData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MiscellaneousInvoiceService {
    
    private final MiscInvoiceRepository repository;

    public MiscellaneousInvoiceService(MiscInvoiceRepository repository) {
        this.repository = repository;
    }
     
    public MiscellaneousInvoice create(MiscellaneousInvoiceData data){
        MiscellaneousInvoice invoice=new MiscellaneousInvoice();
        
        return invoice;
    }
    public MiscellaneousInvoice update(Long id, MiscellaneousInvoiceData data){
        MiscellaneousInvoice invoice=new MiscellaneousInvoice();
        
        return invoice;
    }
    public MiscellaneousInvoice get(Long id){
        MiscellaneousInvoice invoice=new MiscellaneousInvoice();
        
        return invoice;
    }
    public Page<MiscellaneousInvoice> get(Pageable pageable){
        return repository.findAll(pageable);
    }
    public void delete(Long id){
        
    }
}
