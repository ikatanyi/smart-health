package io.smarthealth.accounting.acc.data.mapper;

import io.smarthealth.accounting.acc.data.v1.AccountEntry;
import io.smarthealth.accounting.acc.domain.AccountEntryEntity;
import io.smarthealth.accounting.acc.validation.DateConverter;

public class AccountEntryMapper {

  private AccountEntryMapper() {
    super();
  }

  public static AccountEntry map(final AccountEntryEntity accountEntity) {
    final AccountEntry entry = new AccountEntry();

    entry.setType(accountEntity.getType());
    entry.setBalance(accountEntity.getBalance());
    entry.setAmount(accountEntity.getAmount());
    entry.setMessage(accountEntity.getMessage());
    entry.setTransactionDate(DateConverter.toIsoString(accountEntity.getTransactionDate()));

    return entry;
  }
}
