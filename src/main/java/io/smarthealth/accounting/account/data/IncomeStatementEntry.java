 package io.smarthealth.accounting.account.data;

import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class IncomeStatementEntry {
    @NotEmpty
  private String description;
  @NotNull
  private BigDecimal value;
}
