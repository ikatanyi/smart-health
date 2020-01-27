package io.smarthealth.administration.app.data;

import io.smarthealth.administration.app.domain.BankEmbedded;
import java.io.Serializable;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@Data
public class BankEmbeddedData implements Serializable {

    private String accountName;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
    private String swiftNumber;

    public static BankEmbedded map(BankEmbeddedData data) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(data, BankEmbedded.class);
    }
    
     public static BankEmbeddedData map(BankEmbedded account) {        
        ModelMapper mapper = new ModelMapper();
        return mapper.map(account, BankEmbeddedData.class);
    }
}
