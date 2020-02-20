package io.smarthealth.accounting.cashier.service;
 
import io.smarthealth.accounting.cashier.data.CashPointData;
import io.smarthealth.accounting.cashier.domain.CashPoint;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.accounting.cashier.domain.CashPointRepository;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Kelsas
 */
@Service
//@RequiredArgsConstructor
public class CashPointService {
    private final CashPointRepository repository;

    public CashPointService(CashPointRepository repository) {
        this.repository = repository;
    }
 
    public CashPoint createCashPoint(CashPointData data){
      if(repository.findByName(data.getName()).isPresent()){
          throw APIException.conflict("Cash Point {0} already exists.", data.getName());
      }
       CashPoint cashPoint=new CashPoint();
       cashPoint.setActive(true);
       cashPoint.setName(data.getName());
        
       cashPoint.setTenderTypes(StringUtils.join(data.getTenderTypes(), ','));
       
        return repository.save(cashPoint);
    }
    public Page<CashPoint> fetchAllCashPoints(Pageable page){
        return repository.findAll(page);
    }
    public CashPoint getCashPoint(Long id){
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Cash Drawer with id  {0} not found.", id));
    }
    public CashPoint updateCashPoint(Long id, CashPoint data){
        CashPoint cashPoint=getCashPoint(id);
        if(!cashPoint.getName().equals(data.getName())){
            cashPoint.setName(data.getName());
        }
        
         return repository.save(cashPoint);
    }
}
