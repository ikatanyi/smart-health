/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.data;

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
