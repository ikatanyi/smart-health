package io.smarthealth.financial.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
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
    private String accountType;  
    private String parentAccount;
    private Boolean nonTransacting;
    private Boolean active = true;
    private List<AccountData> children;
    
}
