package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.TransactionType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public final class Debtor {

    private String description;
    private String accountName;
    private String accountNumber;
    private BigDecimal amount;
    private TransactionType transactionType;

    public Debtor() {
        super();
    }

    public Debtor(String description, String accountName, String accountNumber, BigDecimal amount, TransactionType transactionType) {
        this.description = description;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
    }

}
