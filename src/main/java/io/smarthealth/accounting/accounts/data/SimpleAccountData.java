package io.smarthealth.accounting.accounts.data;
 
import io.smarthealth.accounting.accounts.domain.Account;
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

    public static SimpleAccountData map(Account account) {
        SimpleAccountData accdata = new SimpleAccountData();
        accdata.setId(account.getId());
        accdata.setAccountNumber(account.getIdentifier());
        accdata.setAccountName(account.getName());

        return accdata;
    }
}
