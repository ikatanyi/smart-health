package io.smarthealth.administration.app.data;

import io.smarthealth.administration.app.domain.BankAccount;
import java.io.Serializable;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@Data
public class BankAccountData implements Serializable {

    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private String swiftNumber;

    public static BankAccount map(BankAccountData data) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(data, BankAccount.class);
    }
    
     public static BankAccountData map(BankAccount account) {        
        ModelMapper mapper = new ModelMapper();
        return mapper.map(account, BankAccountData.class);
    }
}
