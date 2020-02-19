package io.smarthealth.accounting.accounts.data;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public final class AccountData {

    private AccountType type;
    private String identifier;
    @NotEmpty
    @Length(max = 256)
    private String name;
    @NotNull
    private BigDecimal balance;
    private String referenceAccount;
    private String ledger;
    private AccountState state;
    private String createdBy;

    public static AccountData map(final Account accountEntity) {
        final AccountData account = new AccountData();
        account.setIdentifier(accountEntity.getIdentifier());
        account.setName(accountEntity.getName());
        account.setType(accountEntity.getType());
        account.setLedger(accountEntity.getLedger().getIdentifier());
        if (accountEntity.getReferenceAccount() != null) {
            account.setReferenceAccount(accountEntity.getReferenceAccount().getIdentifier());
        }
        account.setBalance(accountEntity.getBalance());
        account.setCreatedBy(accountEntity.getCreatedBy());
        account.setState(accountEntity.getState());
        return account;
    }
}
