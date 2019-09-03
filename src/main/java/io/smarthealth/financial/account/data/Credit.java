package io.smarthealth.financial.account.data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull; 
import lombok.Value;

/**
 *
 * @author Kelsas
 */
@Value
public class Credit {
    private String accountNumber;
  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  private String amount;  
}
