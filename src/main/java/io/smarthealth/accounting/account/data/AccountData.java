package io.smarthealth.accounting.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.account.domain.Account;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

/**
 *
 * @author kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountData {

    @NotEmpty(message = "Account Number is Required")
    private String accountNumber;
    @NotEmpty(message = "Account Name is Required")
    @Length(max = 150)
    private String accountName; 
    private Long accountType;
    private String accountTypeName;
    private String description;
    private String parentAccount;
    private String parentAccountName;
    private Boolean enabled = true;
    private BigDecimal balance = BigDecimal.ZERO;
    private BigDecimal openingBalance;
    private LocalDate balanceDate;

     public static AccountData map(Account account) {
        AccountData accdata=new AccountData();
        accdata.setAccountType(account.getAccountType().getId());
        accdata.setAccountTypeName(account.getAccountType().getType());
        accdata.setDescription(account.getDescription());
        accdata.setAccountNumber(account.getAccountNumber()); 
        accdata.setAccountName(account.getAccountName());
        accdata.setParentAccount(account.getParentAccount()!=null ? account.getParentAccount().getAccountNumber() : null);
        accdata.setParentAccountName(account.getParentAccount()!=null ? account.getParentAccount().getAccountName(): null);
        accdata.setEnabled(account.getEnabled());
          
        return accdata;
    }

    public static Account map(AccountData data) {
        ModelMapper modelMapper=new ModelMapper();
        Account account = modelMapper.map(data, Account.class); 
        return account;
    }
}
