package io.smarthealth.accounting.acc.data.mapper;


import io.smarthealth.accounting.acc.data.v1.Account;
import io.smarthealth.accounting.acc.domain.AccountEntity;

public class AccountMapper {

  private AccountMapper() {
    super();
  }

  public static Account map(final AccountEntity accountEntity) {
    final Account account = new Account();
    account.setIdentifier(accountEntity.getIdentifier());
    account.setName(accountEntity.getName());
    account.setType(accountEntity.getType());
    account.setLedger(accountEntity.getLedger().getIdentifier());

//    if (accountEntity.getHolders() != null) {
//      account.setHolders(
//              new HashSet<>(Arrays.asList(StringUtils.split(accountEntity.getHolders(), ",")))
//      );
//    }
//
//    if (accountEntity.getSignatureAuthorities() != null) {
//      account.setSignatureAuthorities(
//          new HashSet<>(Arrays.asList(StringUtils.split(accountEntity.getSignatureAuthorities(), ",")))
//      );
//    }
    if (accountEntity.getReferenceAccount() != null) {
      account.setReferenceAccount(accountEntity.getReferenceAccount().getIdentifier());
    }
    account.setBalance(accountEntity.getBalance());
    account.setAlternativeAccountNumber(accountEntity.getAlternativeAccountNumber());
    account.setCreatedBy(accountEntity.getCreatedBy()); 
    if (accountEntity.getLastModifiedBy() != null) {
      account.setLastModifiedBy(accountEntity.getLastModifiedBy()); 
    }
    account.setState(accountEntity.getState());
    return account;
  }
}
