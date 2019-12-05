package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.SimpleAccountData;
import io.smarthealth.accounting.acc.data.mapper.AccountEntryMapper;
import io.smarthealth.accounting.acc.data.mapper.AccountMapper;
import io.smarthealth.accounting.acc.data.v1.Account;
import io.smarthealth.accounting.acc.data.v1.AccountEntry;
import io.smarthealth.accounting.acc.data.v1.AccountEntryPage;
import io.smarthealth.accounting.acc.data.v1.AccountPage;
import io.smarthealth.accounting.acc.data.v1.AccountType;
import io.smarthealth.accounting.acc.domain.AccountEntity;
import io.smarthealth.accounting.acc.domain.AccountEntryEntity;
import io.smarthealth.accounting.acc.domain.AccountEntryRepository;
import io.smarthealth.accounting.acc.domain.LedgerEntity;
import io.smarthealth.accounting.acc.domain.LedgerRepository;
import io.smarthealth.accounting.acc.domain.specification.AccountSpecification;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.acc.domain.AccountRepository;
import io.smarthealth.accounting.acc.domain.AccountsMetadata;
import io.smarthealth.infrastructure.exception.APIException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final AccountEntryRepository accountEntryRepository;

    @Autowired
    public AccountService(final AccountRepository accountRepository,
            final AccountEntryRepository accountEntryRepository,
            LedgerRepository ledgerRepository) {
        super();
        this.accountRepository = accountRepository;
        this.accountEntryRepository = accountEntryRepository;
        this.ledgerRepository = ledgerRepository;
    }

    public Optional<Account> findAccount(final String identifier) {
        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(identifier);
        if (accountEntity == null) {
            return Optional.empty();
        } else {
            return Optional.of(AccountMapper.map(accountEntity));
        }
    }
    
    public Optional<AccountEntity> findByAccountNumber(String identifier){
         final AccountEntity accountEntity = this.accountRepository.findByIdentifier(identifier);
        if (accountEntity == null) {
            return Optional.empty();
        } else {
            return Optional.of(accountEntity);
        }
    }
    public AccountEntity findOneWithNotFoundDetection(String identifier){
        return findByAccountNumber(identifier)
                .orElseThrow(() -> APIException.notFound("Account with Account Number {0} not found", identifier));
    }

    public AccountEntity getAccountEntity(String accountNumber) {
        return this.accountRepository.findByIdentifier(accountNumber);
    }
   public AccountEntity save(AccountEntity entity){
       return this.accountRepository.save(entity);
   }
   public AccountEntryEntity saveAccountEntry(AccountEntryEntity accEntry){
       return accountEntryRepository.save(accEntry);
   }
    public AccountPage fetchAccounts(
            final boolean includeClosed, final String term, final String type,
            final boolean includeCustomerAccounts, final Pageable pageable) {

        final Page<AccountEntity> accountEntities = this.accountRepository.findAll(
                AccountSpecification.createSpecification(includeClosed, term, type, includeCustomerAccounts), pageable
        );

        final AccountPage accountPage = new AccountPage();
        accountPage.setTotalPages(accountEntities.getTotalPages());
        accountPage.setTotalElements(accountEntities.getTotalElements());

        if (accountEntities.getSize() > 0) {
            final List<Account> accounts = new ArrayList<>(accountEntities.getSize());
            accountEntities.forEach(accountEntity -> accounts.add(AccountMapper.map(accountEntity)));
            accountPage.setAccounts(accounts);
        }

        return accountPage;

    }

    public AccountEntryPage fetchAccountEntries(final String identifier,
            final DateRange range,
            final @Nullable String message,
            final Pageable pageable) {

        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(identifier);

        final Page<AccountEntryEntity> accountEntryEntities;
        if (message == null) {
            accountEntryEntities = this.accountEntryRepository.findByAccountAndTransactionDateBetween(
                    accountEntity, range.getStartDateTime(), range.getEndDateTime(), pageable);
        } else {
            accountEntryEntities = this.accountEntryRepository.findByAccountAndTransactionDateBetweenAndMessageEquals(
                    accountEntity, range.getStartDateTime(), range.getEndDateTime(), message, pageable);
        }

        final AccountEntryPage accountEntryPage = new AccountEntryPage();
        accountEntryPage.setTotalPages(accountEntryEntities.getTotalPages());
        accountEntryPage.setTotalElements(accountEntryEntities.getTotalElements());

        if (accountEntryEntities.getSize() > 0) {
            final List<AccountEntry> accountEntries = new ArrayList<>(accountEntryEntities.getSize());
            accountEntryEntities.forEach(accountEntryEntity -> accountEntries.add(AccountEntryMapper.map(accountEntryEntity)));
            accountEntryPage.setAccountEntries(accountEntries);
        }

        return accountEntryPage;
    }

    public Boolean hasEntries(final String identifier) {
        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(identifier);
        return this.accountEntryRepository.existsByAccount(accountEntity);
    }

    public Boolean hasReferenceAccounts(final String identifier) {
        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(identifier);
        return this.accountRepository.existsByReference(accountEntity);
    }

    @Transactional
    public String createAccount(Account account) {
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setIdentifier(account.getIdentifier());
        accountEntity.setName(account.getName());
        accountEntity.setType(account.getType());

        final LedgerEntity ledger = this.ledgerRepository.findByIdentifier(account.getLedger());
        accountEntity.setLedger(ledger);

        AccountEntity referenceAccount = null;
        if (account.getReferenceAccount() != null) {
            referenceAccount = this.accountRepository.findByIdentifier(account.getReferenceAccount());
            if (referenceAccount.getState().equals(Account.State.OPEN.name())) {
                accountEntity.setReferenceAccount(referenceAccount);
            } else {
                throw APIException.badRequest("Reference account {0} is not valid.", referenceAccount.getIdentifier());
            }
        }

//        if (account.getHolders() != null) {
//            accountEntity.setHolders(
//                    account.getHolders()
//                            .stream()
//                            .collect(Collectors.joining(","))
//            );
//        }
//
//        if (account.getSignatureAuthorities() != null) {
//            accountEntity.setSignatureAuthorities(
//                    account.getSignatureAuthorities()
//                            .stream()
//                            .collect(Collectors.joining(","))
//            );
//        }

        accountEntity.setBalance(account.getBalance());
        accountEntity.setState(Account.State.OPEN.name());
        accountEntity.setAlternativeAccountNumber(account.getAlternativeAccountNumber());

        final AccountEntity savedAccountEntity = this.accountRepository.save(accountEntity);

        this.ledgerRepository.save(ledger);

        if (savedAccountEntity.getBalance() != null && savedAccountEntity.getBalance() != 0.00D) {
            this.adjustLedgerTotals(
                    savedAccountEntity.getLedger().getIdentifier(), BigDecimal.valueOf(savedAccountEntity.getBalance()));
        }

        return account.getIdentifier();
    }

    @Transactional
    public String modifyAccount(Account modifyAccountCommand) {
        final Account account = modifyAccountCommand;
        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(account.getIdentifier());

        if (account.getName() != null) {
            accountEntity.setName(account.getName());
        }

        LedgerEntity ledger = null;
        if (!account.getLedger().equals(accountEntity.getLedger().getIdentifier())) {
            ledger = this.ledgerRepository.findByIdentifier(account.getLedger());
            accountEntity.setLedger(ledger);
        }

        AccountEntity referenceAccount = null;
        if (account.getReferenceAccount() != null) {
            if (!account.getReferenceAccount().equals(accountEntity.getReferenceAccount().getIdentifier())) {
                referenceAccount = this.accountRepository.findByIdentifier(account.getReferenceAccount());
                accountEntity.setReferenceAccount(referenceAccount);
            }
        } else {
            accountEntity.setReferenceAccount(null);
        }

//        if (account.getHolders() != null) {
//            accountEntity.setHolders(
//                    account.getHolders()
//                            .stream()
//                            .collect(Collectors.joining(","))
//            );
//        } else {
//            accountEntity.setHolders(null);
//        }
//
//        if (account.getSignatureAuthorities() != null) {
//            accountEntity.setSignatureAuthorities(
//                    account.getSignatureAuthorities()
//                            .stream()
//                            .collect(Collectors.joining(","))
//            );
//        } else {
//            accountEntity.setSignatureAuthorities(null);
//        }

        this.accountRepository.save(accountEntity);

        if (ledger != null) {
            this.ledgerRepository.save(ledger);
        }

        return account.getIdentifier();
    }

    @Transactional
    public String deleteAccount(String accountIdentifier) {
        final AccountEntity accountEntity = this.accountRepository.findByIdentifier(accountIdentifier);

        this.accountRepository.delete(accountEntity);
        return accountIdentifier;
    }

    @Transactional
    public void adjustLedgerTotals(final String ledgerIdentifier, final BigDecimal amount) {
        final LedgerEntity ledger = this.ledgerRepository.findByIdentifier(ledgerIdentifier);
        final BigDecimal currentTotal = ledger.getTotalValue() != null ? ledger.getTotalValue() : BigDecimal.ZERO;
        ledger.setTotalValue(currentTotal.add(amount));
        final LedgerEntity savedLedger = this.ledgerRepository.save(ledger);
        if (savedLedger.getParentLedger() != null) {
            this.adjustLedgerTotals(savedLedger.getParentLedger().getIdentifier(), amount);
        }
    }
     public AccountsMetadata getAccountMetadata() {
        AccountsMetadata metadata = new AccountsMetadata();
        List<SimpleAccountData> income = accountRepository.findByType(AccountType.REVENUE.name())
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        List<SimpleAccountData> expenses = accountRepository.findByType(AccountType.EXPENSE.name())
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        metadata.setIncomeAccounts(income);
        metadata.setExpensesAccounts(expenses);

        return metadata;
    }
}
