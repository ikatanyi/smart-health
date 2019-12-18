package io.smarthealth.accounting.acc.data;
 
import io.smarthealth.accounting.acc.domain.AccountEntity;  
import lombok.Data;

/**
 *
 * @author Kelsas
 */   
@Data
public class SimpleAccountData {

    private Long id;
    private String accountNumber;
    private String accountName;

    public static SimpleAccountData map(AccountEntity account) {
        SimpleAccountData accdata = new SimpleAccountData();
        accdata.setId(account.getId());
        accdata.setAccountNumber(account.getIdentifier());
        accdata.setAccountName(account.getName());

        return accdata;
    }
}
