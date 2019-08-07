package io.smarthealth.financial.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.financial.account.domain.AccountType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountData {  
    private Long id; 
    private String accountCode; 
    private String accountName;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;  
    private String parentAccount;
    private Boolean nonTransacting;
    private Boolean active = true;
    private List<ChildAccount> children=new ArrayList<>();
    
    
}
