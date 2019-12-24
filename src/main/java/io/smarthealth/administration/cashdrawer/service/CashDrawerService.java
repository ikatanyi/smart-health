package io.smarthealth.administration.cashdrawer.service;
 
import io.smarthealth.administration.cashdrawer.domain.CashDrawer;
import io.smarthealth.administration.cashdrawer.domain.CashDrawerRepository;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
//@RequiredArgsConstructor
public class CashDrawerService {
    private final CashDrawerRepository repository;

    public CashDrawerService(CashDrawerRepository repository) {
        this.repository = repository;
    }
 
    public CashDrawer createCashDrawer(CashDrawer cashDrawer){
      if(repository.findByName(cashDrawer.getName()).isPresent()){
          throw APIException.conflict("Cash Drawer {0} already exists.", cashDrawer.getName());
      }
       
        return repository.save(cashDrawer);
    }
    public Page<CashDrawer> fetchAllCashDrawers(Pageable page){
        return repository.findAll(page);
    }
    public CashDrawer getCashDrawer(Long id){
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Cash Drawer with id  {0} not found.", id));
    }
    public CashDrawer updateCashDrawer(Long id, CashDrawer data){
        CashDrawer cashDrawer=getCashDrawer(id);
        if(!cashDrawer.getName().equals(data.getName())){
            cashDrawer.setName(data.getName());
        }
        
        if(!cashDrawer.getReceiptAccount().equals(data.getReceiptAccount())){
            cashDrawer.setReceiptAccount(data.getReceiptAccount());
        }
        
        if(!cashDrawer.getExpenseAccount().equals(data.getExpenseAccount())){
            cashDrawer.setExpenseAccount(data.getExpenseAccount());
        }
         if(!cashDrawer.getOpeningCashAccount().equals(data.getOpeningCashAccount())){
            cashDrawer.setOpeningCashAccount(data.getOpeningCashAccount());
        }
           if(!cashDrawer.getTenderTypes().equals(data.getTenderTypes())){
            cashDrawer.setTenderTypes(data.getTenderTypes());
        }
         return repository.save(cashDrawer);
    }
}
