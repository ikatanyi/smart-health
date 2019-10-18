package io.smarthealth.stock.stores.service;
 
import io.smarthealth.infrastructure.exception.APIException; 
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }
    public Store createStore(Store store){
      if(storeRepository.findByStoreName(store.getStoreName()).isPresent()){
          throw APIException.conflict("Store {0} already exists.", store.getStoreName());
      }
       
        return storeRepository.save(store);
    }
    public Page<Store> fetchAllStores(Pageable page){
        return storeRepository.findAll(page);
    }
    public Optional<Store> getStore(Long id){
        return storeRepository.findById(id);
    }
}
