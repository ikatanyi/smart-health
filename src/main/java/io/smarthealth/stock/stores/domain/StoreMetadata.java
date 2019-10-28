package io.smarthealth.stock.stores.domain;

import io.smarthealth.accounting.account.domain.Account;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StoreMetadata {

    private String code="0";
    private String message ="success"; 
    private List<Account> salesAccount; 
    private List<Account> purchaseAccount; 
    private List<Account> inventoryAccount;
    
}
