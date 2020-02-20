package io.smarthealth.accounting.accounts.data;


import java.math.BigDecimal;
import lombok.Data;


@Data
public final class Creditor {
    private String description;
    private String accountName;
  private String accountNumber; 
  private BigDecimal amount;

  public Creditor() {
    super();
  }

  public Creditor(String description,String accountNumber, BigDecimal amount) {
      this.description=description;
    this.accountNumber = accountNumber;
    this.amount = amount;
  }
 
}
