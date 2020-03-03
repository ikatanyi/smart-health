package io.smarthealth.stock.stores.data;

import io.smarthealth.stock.stores.domain.Store;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StoreData {
   private Long id;
    private String storeType;
    private String storeName;
    private boolean patientStore;
    private Long servicePointId;
    private String servicePoint; 
    private String inventoryAccountNumber;
    private String inventoryAccount;
    private String expenseAccountNumber;
    private String expenseAccount;
    private boolean active;
    
    public static StoreData map(Store store ){
        StoreData data=new StoreData();
        data.setId(store.getId());
        data.setActive(store.isActive());
        data.setPatientStore(store.isPatientStore());
        data.setStoreType(store.getStoreType().name());
        data.setStoreName(store.getStoreName());
        if(store.getInventoryAccount()!=null){
            data.setInventoryAccount(store.getInventoryAccount().getName());
            data.setInventoryAccountNumber(store.getInventoryAccount().getIdentifier());
        }
         if(store.getExpenseAccount()!=null){
            data.setExpenseAccount(store.getExpenseAccount().getName());
            data.setExpenseAccountNumber(store.getExpenseAccount().getIdentifier());
        }
        if(store.getServicePoint()!=null){
            data.setServicePoint(store.getServicePoint().getName());
             data.setServicePointId(store.getServicePoint().getId());
        }

          return data;
    }
}
