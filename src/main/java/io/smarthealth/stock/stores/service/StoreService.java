package io.smarthealth.stock.stores.service;

import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.domain.IncomeExpenseData;
import io.smarthealth.accounting.acc.service.AccountService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreRepository;
import java.util.List;
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
    private final AccountService accountService;

    public StoreService(StoreRepository storeRepository, AccountService accountService) {
        this.storeRepository = storeRepository;
        this.accountService = accountService;
    }
 

    public Store createStore(StoreData data) {
        if (storeRepository.findByStoreName(data.getStoreName()).isPresent()) {
            throw APIException.conflict("Store {0} already exists.", data.getStoreName());
        }
        Store toSave = new Store();
        toSave.setActive(true);
        toSave.setStoreType(Store.Type.valueOf(data.getStoreType()));
        toSave.setStoreName(data.getStoreName());
        toSave.setPatientStore(data.isPatientStore());

        if (data.getSalesAccountNumber()!= null) {
            Optional< AccountEntity> sales = accountService.findByAccountNumber(data.getSalesAccountNumber());
            if (sales.isPresent()) {
                toSave.setSalesAccount(sales.get());
            }
        }

        if (data.getPurchaseAccountNumber() != null) {
            Optional< AccountEntity> purchase = accountService.findByAccountNumber(data.getPurchaseAccountNumber());
            if (purchase.isPresent()) {
                toSave.setPurchaseAccount(purchase.get());
            }
        }
        if (data.getInventoryAccountNumber() != null) {
            Optional< AccountEntity> inventory = accountService.findByAccountNumber(data.getInventoryAccountNumber());
            if (inventory.isPresent()) {
                toSave.setInventoryAccount(inventory.get());
            }
        }

        return storeRepository.save(toSave);
    }

    public Page<Store> fetchAllStores(Pageable page) {
        return storeRepository.findAll(page);
    }

    public Optional<Store> getStore(Long id) {
        return storeRepository.findById(id);
    }

    public Store getStoreWithNoFoundDetection(Long id) {
        return getStore(id)
                .orElseThrow(() -> APIException.notFound("Store with id {0} not found", id));
    }

    public List<StoreData> getAllStores() {
        Page<StoreData> stores = storeRepository.findAll(Pageable.unpaged()).map(store -> StoreData.map(store));
        return stores.getContent();
    }

    public IncomeExpenseData getStoreMetadata() {
        return accountService.getIncomeExpenseAccounts();
    }
}
