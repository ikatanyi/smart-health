/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import io.smarthealth.accounting.account.data.AccountData; 
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TrialBalanceEntry {
    private AccountData accountData;
  private Type type;
  private BigDecimal amount;
  public enum Type {
    DEBIT,
    CREDIT
  }
}
