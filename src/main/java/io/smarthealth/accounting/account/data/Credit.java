package io.smarthealth.accounting.account.data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull; 
import lombok.Data;
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Data
public class Credit {
    private String accountNumber;
  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  private String amount; 
  private String description;

    public Credit() {
    }

    public Credit(String accountNumber, String amount, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
    }
  
}
