package io.smarthealth.financial.account.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 *
 * @author kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountData {

    public enum State {
        OPEN,
        LOCKED,
        CLOSED
    }
    private AccountType type;
    private String identifier; //accountCode
    @NotEmpty
    @Length(max = 256)
    private String name; //accountName
    private Double balance=0.00D;
    private String referenceAccount;
    private String ledger;
    private State state;
     
}
