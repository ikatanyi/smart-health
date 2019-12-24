package io.smarthealth.accounting.acc.data.v1.financial.statement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class IncomeStatementSection {

  public enum Type {
    INCOME,
    EXPENSES
  }

  @NotEmpty
  private Type type;
  @NotEmpty
  private String description;
  @NotEmpty
  private List<IncomeStatementEntry> incomeStatementEntries = new ArrayList<>();
  @NotNull
  private BigDecimal subtotal = BigDecimal.ZERO;

  public IncomeStatementSection() {
    super();
  }

  public String getType() {
    return this.type.name();
  }

  public void setType(final String type) {
    this.type = Type.valueOf(type);
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<IncomeStatementEntry> getIncomeStatementEntries() {
    return this.incomeStatementEntries;
  }

  public BigDecimal getSubtotal() {
    return this.subtotal;
  }

  public void add(final IncomeStatementEntry incomeStatementEntry) {
    this.incomeStatementEntries.add(incomeStatementEntry);
    this.subtotal = this.subtotal.add(incomeStatementEntry.getValue());
  }
}
