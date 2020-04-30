package io.smarthealth.accounting.taxes.service;
 
import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.taxes.domain.TaxRepository;

/**
 *
 * @author Kelsas
 */
@Service
public class TaxServices {
    private final TaxRepository taxRepository;

    public TaxServices(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }
    public Tax createTax(Tax tax){
      if(taxRepository.findByTaxName(tax.getTaxName()).isPresent()){
          throw APIException.conflict("Tax {0} already exists.", tax.getTaxName());
      }
       
        return taxRepository.save(tax);
    }
    public Page<Tax> fetchAllTaxes(Pageable page){
        return taxRepository.findAll(page);
    }
    public Tax getTax(Long id){
        return taxRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Tax with id  {0} not found.", id));
    }
    public Tax updateTax(Long id, Tax data){
        Tax tax=getTax(id);
        if(!tax.getTaxName().equals(data.getTaxName())){
            tax.setTaxName(data.getTaxName());
        }
         if(tax.getRate()!=(data.getRate())){
            tax.setRate(data.getRate());
        }
         return taxRepository.save(tax);
    }
}
