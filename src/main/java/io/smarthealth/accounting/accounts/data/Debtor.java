package io.smarthealth.accounting.accounts.data;


import java.math.BigDecimal;
import lombok.Data;

@Data
public final class Debtor {
    private String description;
    private String accountName; 
  private String accountNumber; 
  private BigDecimal amount;

  public Debtor() {
    super();
  }

  public Debtor(String description,String accountNumber, BigDecimal amount) {
      this.description=description;
    this.accountNumber = accountNumber;
    this.amount = amount;
  }
 
}
