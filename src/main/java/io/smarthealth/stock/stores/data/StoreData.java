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
    private String salesAccountNumber;
    private String salesAccount;
    private String purchaseAccountNumber;
    private String purchaseAccount;
    private String inventoryAccountNumber;
    private String inventoryAccount;
    private boolean active;
    
    public static StoreData map(Store store ){
        StoreData data=new StoreData();
        data.setId(store.getId());
        data.setActive(store.isActive());
        data.setPatientStore(store.isPatientStore());
        data.setStoreType(store.getStoreType().name());
        data.setStoreName(store.getStoreName());
        if(store.getInventoryAccount()!=null){
            data.setInventoryAccount(store.getInventoryAccount().getAccountName());
            data.setInventoryAccountNumber(store.getInventoryAccount().getAccountNumber());
        }
         if(store.getSalesAccount()!=null){
            data.setSalesAccount(store.getSalesAccount().getAccountName());
            data.setSalesAccountNumber(store.getSalesAccount().getAccountNumber());
        }
          if(store.getPurchaseAccount()!=null){
            data.setPurchaseAccount(store.getPurchaseAccount().getAccountName());
            data.setPurchaseAccountNumber(store.getPurchaseAccount().getAccountNumber());
        }
          return data;
    }
}
