package io.smarthealth.accounting.acc.data.v1;


import java.util.Objects;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import org.smarthealth.patient.validation.constraints.ValidIdentifier;


@SuppressWarnings({"unused", "WeakerAccess"})
public final class Creditor {
    private String accountName;
  @ValidIdentifier(maxLength = 34)
  private String accountNumber;
  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  private String amount;

  public Creditor() {
    super();
  }

  public Creditor(String accountNumber, String amount) {
    this.accountNumber = accountNumber;
    this.amount = amount;
  }

  public String getAccountNumber() {
    return this.accountNumber;
  }

  public void setAccountNumber(final String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getAmount() {
    return this.amount;
  }

  public void setAmount(final String amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Creditor creditor = (Creditor) o;
    return Objects.equals(accountNumber, creditor.accountNumber) &&
            Objects.equals(amount, creditor.amount);
  }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

  @Override
  public int hashCode() {
    return Objects.hash(accountNumber, amount);
  }

  @Override
  public String toString() {
    return "Creditor{" +
            "accountNumber='" + accountNumber + '\'' +
            ", amount='" + amount + '\'' +
            '}';
  }
}
