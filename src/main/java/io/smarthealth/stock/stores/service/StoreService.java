package io.smarthealth.stock.stores.service;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.IncomeExpenseData;
import io.smarthealth.accounting.accounts.service.AccountService;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.Store.Type;
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
    private final ServicePointService servicePointService;

    public StoreService(StoreRepository storeRepository, AccountService accountService, ServicePointService servicePointService) {
        this.storeRepository = storeRepository;
        this.accountService = accountService;
        this.servicePointService = servicePointService;
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
        if (data.getServicePointId() != null) {
            ServicePoint srv = servicePointService.getServicePoint(data.getServicePointId());
            toSave.setServicePoint(srv);
        }

        if (data.getInventoryAccountNumber() != null) {
            Optional< Account> inventory = accountService.findByAccountNumber(data.getInventoryAccountNumber());
            if (inventory.isPresent()) {
                toSave.setInventoryAccount(inventory.get());
            }
        }

        if (data.getExpenseAccountNumber() != null) {
            Optional< Account> expense = accountService.findByAccountNumber(data.getExpenseAccountNumber());
            if (expense.isPresent()) {
                toSave.setExpenseAccount(expense.get());
            }
        }

        return storeRepository.save(toSave);
    }

    public Store updateStore(Long storeId, StoreData data) {

        Store toSave = getStoreWithNoFoundDetection(storeId);

        toSave.setActive(data.isActive());
        toSave.setStoreType(Store.Type.valueOf(data.getStoreType()));
        toSave.setStoreName(data.getStoreName());
        toSave.setPatientStore(data.isPatientStore());
        if (data.getServicePointId() != null) {
            ServicePoint srv = servicePointService.getServicePoint(data.getServicePointId());
            toSave.setServicePoint(srv);
        }

        if (data.getInventoryAccountNumber() != null) {
            Optional< Account> inventory = accountService.findByAccountNumber(data.getInventoryAccountNumber());
            if (inventory.isPresent()) {
                toSave.setInventoryAccount(inventory.get());
            }
        }

        if (data.getExpenseAccountNumber() != null) {
            Optional< Account> expense = accountService.findByAccountNumber(data.getExpenseAccountNumber());
            if (expense.isPresent()) {
                toSave.setExpenseAccount(expense.get());
            }
        }

        return storeRepository.save(toSave);
    }

    public Page<Store> fetchAllStores(Boolean patientStore, Pageable page) {
        if (patientStore != null) {
            return storeRepository.findByPatientStore(patientStore, page);
        }
        return storeRepository.findAll(page);
    }

    public Optional<Store> getStore(Long id) {
        return storeRepository.findById(id);
    }

    public Store getMainStore(Type type) {
        return storeRepository.findByStoreType(type.MainStore)
                .orElseThrow(() -> APIException.notFound("Main Store not found", ""));
    }

    public List<Store> findActiveStores() {
        return storeRepository.findByActiveTrue();
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
