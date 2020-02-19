package io.smarthealth.accounting.accounts.data.financial.statement;
 
import io.smarthealth.accounting.accounts.data.LedgerData;
import java.math.BigDecimal;

@SuppressWarnings("WeakerAccess")
public class TrialBalanceEntry {

  private LedgerData ledger;
  private Type type;
  private BigDecimal amount;

  public TrialBalanceEntry() {
    super();
  }

  public LedgerData getLedger() {
    return this.ledger;
  }

  public void setLedger(final LedgerData ledger) {
    this.ledger = ledger;
  }

  public String getType() {
    return this.type.name();
  }

  public void setType(final String type) {
    this.type = Type.valueOf(type);
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }

  public enum Type {
    DEBIT,
    CREDIT
  }
}
