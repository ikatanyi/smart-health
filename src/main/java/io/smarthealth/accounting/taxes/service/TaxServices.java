package io.smarthealth.accounting.taxes.service;
 
import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.accounting.taxes.domain.TaxRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Optional<Tax> getTax(Long id){
        return taxRepository.findById(id);
    }
}
