package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.AccountData;
import io.smarthealth.accounting.accounts.data.AccountGroups;
import io.smarthealth.accounting.accounts.data.AccountPage;
import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.IncomeExpenseData;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.accounting.accounts.domain.specification.AccountSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;

    public Optional<AccountData> findAccount(final String identifier) {
        final Optional<Account> accountEntity = this.accountRepository.findByIdentifier(identifier);
        if (accountEntity.isPresent()) {
            return Optional.of(AccountData.map(accountEntity.get()));
        }
        return Optional.empty();
    }

    public Optional<Account> findByAccountNumber(String identifier) {
        return accountRepository.findByIdentifier(identifier);
    }

    public Account findByAccountNumberOrThrow(String identifier) {
        return findByAccountNumber(identifier)
                .orElseThrow(() -> APIException.notFound("Account with Account Number {0} not found", identifier));
    }

    public Account save(Account entity) {
        return this.accountRepository.save(entity);
    }

    public AccountPage fetchAccounts(
            final boolean includeClosed, final String term, final AccountType type,
            final boolean includeCustomerAccounts, final Pageable pageable) {

        final Page<Account> accountEntities = this.accountRepository.findAll(
                AccountSpecification.createSpecification(includeClosed, term, type, includeCustomerAccounts), pageable
        );

        final AccountPage accountPage = new AccountPage();
        accountPage.setTotalPages(accountEntities.getTotalPages());
        accountPage.setTotalElements(accountEntities.getTotalElements());

        if (accountEntities.getSize() > 0) {
            final List<AccountData> accounts = new ArrayList<>(accountEntities.getSize());
            accountEntities.forEach(accountEntity -> accounts.add(AccountData.map(accountEntity)));
            accountPage.setAccounts(accounts);
        }
        return accountPage;
    }

    @Transactional
    public String createAccount(AccountData account) {
        final Account accountEntity = new Account();
        accountEntity.setIdentifier(account.getIdentifier());
        accountEntity.setName(account.getName());
        accountEntity.setType(account.getType());

        final Ledger ledger = findLedgerByIdOrThrow(account.getLedger());

        accountEntity.setLedger(ledger);

        if (account.getReferenceAccount() != null) {
            Account referenceAccount = findByAccountNumberOrThrow(account.getReferenceAccount());

            if (referenceAccount.getState().equals(AccountState.OPEN)) {
                accountEntity.setReferenceAccount(referenceAccount);
            } else {
                throw APIException.badRequest("Reference account {0} is not valid.", referenceAccount.getIdentifier());
            }
        }
        accountEntity.setBalance(account.getBalance());
        accountEntity.setState(AccountState.OPEN);

        final Account savedAccount = this.accountRepository.save(accountEntity);

        this.ledgerRepository.save(ledger);

        if (savedAccount.getBalance() != null && savedAccount.getBalance() != BigDecimal.ZERO) {
            this.adjustLedgerTotals(
                    savedAccount.getLedger().getIdentifier(), savedAccount.getBalance());
        }

        return account.getIdentifier();
    }

    @Transactional
    public String modifyAccount(AccountData account) {
        final Account accountEntity = findByAccountNumberOrThrow(account.getIdentifier());

        if (account.getName() != null) {
            accountEntity.setName(account.getName());
        }

        Ledger ledger = null;
        if (!account.getLedger().equals(accountEntity.getLedger().getIdentifier())) {
            ledger = findLedgerByIdOrThrow(account.getLedger());
            accountEntity.setLedger(ledger);
        }

        if (account.getReferenceAccount() != null) {
            if (!account.getReferenceAccount().equals(accountEntity.getReferenceAccount().getIdentifier())) {
                Account referenceAccount = findByAccountNumberOrThrow(account.getReferenceAccount());
                accountEntity.setReferenceAccount(referenceAccount);
            }
        } else {
            accountEntity.setReferenceAccount(null);
        }
        this.accountRepository.save(accountEntity);

        if (ledger != null) {
            this.ledgerRepository.save(ledger);
        }

        return account.getIdentifier();
    }

    @Transactional
    public String deleteAccount(String accountIdentifier) {
        final Account accountEntity = findByAccountNumberOrThrow(accountIdentifier);
        this.accountRepository.delete(accountEntity);
        return accountIdentifier;
    }

    @Transactional
    public void adjustLedgerTotals(final String ledgerIdentifier, final BigDecimal amount) {
        final Ledger ledger = findLedgerByIdOrThrow(ledgerIdentifier);
        final BigDecimal currentTotal = ledger.getTotalValue() != null ? ledger.getTotalValue() : BigDecimal.ZERO;
        ledger.setTotalValue(currentTotal.add(amount));
        final Ledger savedLedger = this.ledgerRepository.save(ledger);
        if (savedLedger.getParentLedger() != null) {
            this.adjustLedgerTotals(savedLedger.getParentLedger().getIdentifier(), amount);
        }
    }

    public Ledger findLedgerByIdOrThrow(String ledgerIdentifier) {
        return ledgerRepository.findByIdentifier(ledgerIdentifier)
                .orElseThrow(() -> APIException.notFound("Ledger with id {0} not found", ledgerIdentifier));
    }

    public IncomeExpenseData getIncomeExpenseAccounts() {
        IncomeExpenseData metadata = new IncomeExpenseData();
        List<SimpleAccountData> income = accountRepository.findByType(AccountType.REVENUE)
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        List<SimpleAccountData> expenses = accountRepository.findByType(AccountType.EXPENSE)
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        metadata.setIncomeAccounts(income);
        metadata.setExpensesAccounts(expenses);

        return metadata;
    }

    public List<SimpleAccountData> getTransactionalAccounts(AccountType type) {
        List<Account> accounts;
        if (type != null) {
            accounts = accountRepository.findByType(type);
        } else {
            accounts = accountRepository.findAll();
        }

        return accounts
                .stream()
                .map(x -> SimpleAccountData.map(x))
                .collect(Collectors.toList());
    }

    public AccountGroups getGroupedAccounts() {
        AccountGroups groups = new AccountGroups();
        accountRepository.findAll()
                .stream()
                .forEach(x -> {
                    SimpleAccountData sad = SimpleAccountData.map(x);
                    switch (x.getType()) {

                        case ASSET: {
                            groups.getAssets().add(sad);
                            break;
                        }
                        case LIABILITY: {
                            groups.getLiabilities().add(sad);
                            break;
                        }
                        case EQUITY: {
                            groups.getEquity().add(sad);
                            break;
                        }
                        case REVENUE: {
                            groups.getRevenue().add(sad);
                            break;
                        }
                        case EXPENSE: {
                            groups.getExpenses().add(sad);
                            break;
                        }
                        default:
                    }
                });
        return groups;
    }
}
