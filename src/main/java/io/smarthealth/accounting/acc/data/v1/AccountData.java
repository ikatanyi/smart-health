package io.smarthealth.accounting.acc.data.v1;
 
import lombok.Value;


/**
 *
 * @author Kelsas
 */
@Value
public class AccountData {
     private Long id;
    private String accountNumber;
    private String accountName;

}
