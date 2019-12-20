/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class IncomeStatement {
  private LocalDateTime date;
  @NotEmpty
  private List<IncomeStatementSection> incomeStatementSections = new ArrayList<>();
  @NotNull
  private BigDecimal grossProfit;
  @NotNull
  private BigDecimal totalExpenses;
  @NotNull
  private BigDecimal netIncome;
  
  public void add(final IncomeStatementSection incomeStatementSection) {
    this.incomeStatementSections.add(incomeStatementSection);
  }
}
