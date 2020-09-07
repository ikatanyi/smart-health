package io.smarthealth.accounting.accounts.service;

import io.smarthealth.accounting.accounts.data.AccountBalance;
import io.smarthealth.accounting.accounts.data.AccountData;
import io.smarthealth.accounting.accounts.data.AccountGroups;
import io.smarthealth.accounting.accounts.data.AccountPage;
import io.smarthealth.accounting.accounts.data.JournalEntryItemData;
import io.smarthealth.accounting.accounts.data.SimpleAccountData;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.AccountRepository;
import io.smarthealth.accounting.accounts.domain.AccountState;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.IncomeExpenseData;
import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalEntryItemRepository;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.Ledger;
import io.smarthealth.accounting.accounts.domain.LedgerRepository;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.domain.specification.AccountSpecification;
import io.smarthealth.accounting.accounts.domain.specification.JournalSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.AccBalanceData;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.DateUtility;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final JournalEntryItemRepository journalEntryItemRepository;

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
            final Boolean includeClosed, final String term, final AccountType type,
            final Boolean includeCustomerAccounts, final Pageable pageable) {

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
    public Account createAccount(AccountData account) {
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

        return savedAccount;
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

    public Page<JournalEntryItem> getAccountEntries(String identifier, Pageable page) {
        final Account accountEntity = findByAccountNumberOrThrow(identifier);
        return journalEntryItemRepository.findByAccount(accountEntity, page);
    }

    public BigDecimal getAccountBalance(String identifier, LocalDate date) {
        return journalEntryItemRepository.getAccountsBalance(identifier, date);
    }

    public BigDecimal getAccountBalance(String identifier, DateRange period) {
        return journalEntryItemRepository.getAccountsBalance(identifier, period);
    }

    public AccountBalance fetchAccountBalance(String identifier, LocalDate date, DateRange period) {
        Account account = findByAccountNumberOrThrow(identifier);
        String description;
        BigDecimal balance;

        if (period != null) {
            description = String.format("Period : %s to %s", period.getStartDate().format(DateTimeFormatter.ISO_DATE), period.getEndDate().format(DateTimeFormatter.ISO_DATE));
            balance = getAccountBalance(identifier, period);
        } else {
            LocalDate dte = date == null ? LocalDate.now() : date;
            description = String.format("Balance as at : %s", dte.format(DateTimeFormatter.ISO_DATE));
            balance = getAccountBalance(identifier, date);
        }

        return new AccountBalance(account.getIdentifier(), account.getName(), description, balance);
    }

//    public TransactionList getTransactionLists(String identifier, DateRange period) {
//        //Get the list of the items first
//        LocalDate startDate = (period == null ? DateUtility.getStartOfCurrentMonth() : period.getStartDate());
//        LocalDate endDate = (period == null ? DateUtility.getEndOfCurrentMonth() : period.getEndDate());
//
//        AccountBalance balance = fetchAccountBalance(identifier, startDate.minusDays(1), null);
//
//        BigDecimal bal;
//        List<JournalEntryItemData> transactions = journalEntryItemRepository.findAll(JournalSpecification.getTransactions(identifier, startDate, endDate))
//                .stream()
//                .map(x -> x.toData())
//                .collect(Collectors.toList());
//        TransactionList list = new TransactionList();
//        list.setBalanceBroughtForward(balance);
//        list.setTransactions(transactions);
//
//        return list;
//    }
    public List<JournalEntryItemData> getAccountTransaction(String identifier, DateRange period) {

        LocalDate startDate = (period == null ? DateUtility.getStartOfCurrentMonth() : period.getStartDate());
        LocalDate endDate = (period == null ? DateUtility.getEndOfCurrentMonth() : period.getEndDate());

        BigDecimal bal = toDefault(getAccountBalance(identifier, startDate.minusDays(1)));

        List<JournalEntryItemData> list = new ArrayList<>();

        list.add(openingEntry(identifier, startDate.minusDays(1), bal));

        List<JournalEntryItem> transactions = journalEntryItemRepository.findAll(JournalSpecification.getTransactions(identifier, startDate, endDate));

        for (JournalEntryItem x : transactions) {
            if (x.getAccount().getType() == AccountType.REVENUE || x.getAccount().getType() == AccountType.LIABILITY) {
                bal = bal.add((toDefault(x.getCredit()).subtract(toDefault(x.getDebit()))));
            } else {
                bal = bal.add((toDefault(x.getDebit()).subtract(toDefault(x.getCredit()))));
            }
            JournalEntryItemData data = x.toData();
            data.setAmount(bal);
            list.add(data);
        }

        //period details From 01 May 2020 To 31 May 2020
        return list;
    }

    private JournalEntryItemData openingEntry(String identifier, LocalDate entryDate, BigDecimal balance) {
        Account account = findByAccountNumber(identifier).orElse(null);

        JournalEntryItemData data = new JournalEntryItemData();

        data.setDate(entryDate);
        if (account != null) {
            data.setAccountName(account.getName());
            data.setAccountNumber(account.getIdentifier());
        }
        data.setCredit(BigDecimal.ZERO);
        data.setDebit(BigDecimal.ZERO);
        data.setAmount(balance);
        data.setDescription("Balance b/f");
        data.setJournalId(0L);
        data.setStatus(JournalState.PROCESSED);
        data.setTransactionNo(entryDate.toString());
        data.setCreatedBy("");
        data.setType(TransactionType.Balance_Brought_Forward);

        return data;
    }

    public List<JournalEntryItem> openingBatchEntry(List<AccBalanceData> accs) {
//        List<JournalEntry> entries =new ArrayList();
        List<JournalEntryItem> itemArray = new ArrayList();
        for (AccBalanceData acc : accs) {
            Account account = findByAccountNumber(acc.getIdentifier()).orElse(null);
            JournalEntryItem data = new JournalEntryItem(account, "Balance b/f", BigDecimal.ZERO, acc.getBalance());

            itemArray.add(data);
        }
        return journalEntryItemRepository.saveAll(itemArray);
    }

    private BigDecimal toDefault(BigDecimal val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }

        return val;
    }
}
