/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import io.smarthealth.financial.account.data.LedgerData;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TrialBalanceEntry {
    private LedgerData ledger;
  private Type type;
  private BigDecimal amount;
  public enum Type {
    DEBIT,
    CREDIT
  }
}
