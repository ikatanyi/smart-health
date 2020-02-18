package io.smarthealth.accounting.accounts.data.financial.statement;


import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class FinancialConditionEntry {

  @NotEmpty
  private String description;
  @NotNull
  private BigDecimal value;

  public FinancialConditionEntry() {
    super();
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public void setValue(final BigDecimal value) {
    this.value = value;
  }
}
