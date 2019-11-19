package io.smarthealth.stock.stores.service;

import io.smarthealth.accounting.account.data.AccountData;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.domain.StoreMetadata;
import io.smarthealth.stock.stores.domain.StoreRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final AccountRepository accountRepository;

    public StoreService(StoreRepository storeRepository, AccountRepository accountRepository) {
        this.storeRepository = storeRepository;
        this.accountRepository = accountRepository;
    }

    public Store createStore(StoreData store) {
        if (storeRepository.findByStoreName(store.getStoreName()).isPresent()) {
            throw APIException.conflict("Store {0} already exists.", store.getStoreName());
        }
        Store toSave = new Store();
        toSave.setActive(true);
        toSave.setStoreType(Store.Type.valueOf(store.getStoreType()));
        toSave.setStoreName(store.getStoreName());
        toSave.setPatientStore(store.isPatientStore());

        if (store.getSalesAccountId() != null) {
            Optional< Account> sales = accountRepository.findByAccountNumber(store.getSalesAccountId());
            if (sales.isPresent()) {
                toSave.setSalesAccount(sales.get());
            }
        }

        if (store.getSalesAccountId() != null) {
            Optional< Account> purchase = accountRepository.findByAccountNumber(store.getPurchaseAccountId());
            if (purchase.isPresent()) {
                toSave.setPurchaseAccount(purchase.get());
            }
        }
        if (store.getSalesAccountId() != null) {
            Optional< Account> inventory = accountRepository.findByAccountNumber(store.getInventoryAccountId());
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

    public StoreMetadata getStoreMetadata() {
        StoreMetadata metadata = new StoreMetadata();
        List<AccountData> income = accountRepository.findParentAccountIsNullAndAccountCategory(AccountCategory.REVENUE)
                .stream()
                .map(acc -> AccountData.map(acc))
                .collect(Collectors.toList());
        List<AccountData> expenses = accountRepository.findParentAccountIsNullAndAccountCategory(AccountCategory.EXPENSE)
                .stream()
                .map(acc -> AccountData.map(acc))
                .collect(Collectors.toList());
        metadata.setIncomeAccounts(income);
        metadata.setExpensesAccounts(expenses);

        return metadata;
    }
}
