package io.smarthealth.accounting.accounts.data;
 
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(required=false, hidden=true)
    private AccountType type;

    public static SimpleAccountData map(Account account) {
        SimpleAccountData accdata = new SimpleAccountData();
        accdata.setId(account.getId());
        accdata.setAccountNumber(account.getIdentifier());
        accdata.setAccountName(account.getName());
        accdata.setType(account.getType());

        return accdata;
    }
}
