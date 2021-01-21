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
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.infrastructure.utility.DateUtility;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final AuditTrailService auditTrailService;

    public Optional<AccountData> findAccount(final String identifier) {
        final Optional<Account> accountEntity = this.accountRepository.findByIdentifier(identifier);
        auditTrailService.saveAuditTrail("Accounts", "Searched account by accountNo. " + identifier);
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
        auditTrailService.saveAuditTrail("Accounts", "Created Account " + account.getName());
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
        auditTrailService.saveAuditTrail("Accounts", "Edited Account " + accountEntity.getName());
        return account.getIdentifier();
    }

    @Transactional
    public String deleteAccount(String accountIdentifier) {
        final Account accountEntity = findByAccountNumberOrThrow(accountIdentifier);
        this.accountRepository.delete(accountEntity);
        auditTrailService.saveAuditTrail("Accounts", "Deleted Account " + accountEntity.getName());
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
        auditTrailService.saveAuditTrail("Accounts", "Adjusted Ledger Totals for ledger " + ledger.getName() + " from " + currentTotal + " to" + amount);
    }

    public Ledger findLedgerByIdOrThrow(String ledgerIdentifier) {
        auditTrailService.saveAuditTrail("Accounts", "Searched account " + ledgerIdentifier);
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
        auditTrailService.saveAuditTrail("Accounts", "viewed Income Expense");
        return metadata;

    }

    public List<SimpleAccountData> getTransactionalAccounts(AccountType type) {
        List<Account> accounts;
        if (type != null) {
            accounts = accountRepository.findByType(type);
        } else {
            accounts = accountRepository.findAll();
        }
        auditTrailService.saveAuditTrail("Accounts", "Transactional Accounts");

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
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account Groups");
        return groups;
    }

    public Page<JournalEntryItem> getAccountEntries(String identifier, Pageable page) {
        final Account accountEntity = findByAccountNumberOrThrow(identifier);
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account Entries");
        return journalEntryItemRepository.findByAccount(accountEntity, page);
    }

    public BigDecimal getAccountBalance(String identifier, LocalDate date) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account Balance for account " + identifier + " for date " + date);
        return journalEntryItemRepository.getAccountsBalance(identifier, date);
    }

    public BigDecimal getAccountBalance(String identifier, DateRange period) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account Balance for account " + identifier + " for a period of " + period);
        return journalEntryItemRepository.getAccountsBalance(identifier, period);
    }

    public AccountBalance fetchAccountBalance(String identifier, LocalDate date, DateRange period) {
        Account account = findByAccountNumberOrThrow(identifier);
        String description;
        BigDecimal balance;

        if (period != null) {
            description = String.format("Period : %s to %s", period.getStartDate().format(DateTimeFormatter.ISO_DATE), period.getEndDate().format(DateTimeFormatter.ISO_DATE));
            balance = getAccountBalance(identifier, period);
            auditTrailService.saveAuditTrail("Accounts", "Viewed Account Balance for account " + identifier + " for a period of " + period);
        } else {
            LocalDate dte = date == null ? LocalDate.now() : date;
            description = String.format("Balance as at : %s", dte.format(DateTimeFormatter.ISO_DATE));
            balance = getAccountBalance(identifier, date);
            auditTrailService.saveAuditTrail("Accounts", "Viewed Account Balance for account " + identifier + " for date " + date);
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
        log.info("Transaction start date {} and end date {}  ", startDate.toString(), endDate.toString());
        //startDate.minusDays(1)
        BigDecimal bal = toDefault(getAccountBalance(identifier, startDate.minusDays(1)));

        List<JournalEntryItemData> list = new ArrayList<>();

        list.add(openingEntry(identifier, startDate, bal));

        List<JournalEntryItem> transactions = journalEntryItemRepository.findAll(JournalSpecification.getTransactions(identifier, startDate, endDate))
                .stream()
                .sorted(Comparator.comparing(j -> j.getJournalEntry().getDate()))
                .collect(Collectors.toList());

        for (JournalEntryItem x : transactions) {
            if (x.getAccount().getType() == AccountType.ASSET || x.getAccount().getType() == AccountType.EXPENSE) {
                bal = bal.add((toDefault(x.getDebit()).subtract(toDefault(x.getCredit()))));
            } else {
                bal = bal.add((toDefault(x.getCredit()).subtract(toDefault(x.getDebit()))));
            }
            JournalEntryItemData data = x.toData();
            data.setAmount(bal);
            list.add(data);
        }
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account Transactions for account " + identifier + " for the period between " + period);
        return list;
    }

    private JournalEntryItemData openingEntry(String identifier, LocalDate entryDate, BigDecimal balance) {
        Account account = findByAccountNumber(identifier).orElse(null);

        JournalEntryItemData data = new JournalEntryItemData();
        // I need to get the balances for each debit or 
        data.setDate(entryDate.with(TemporalAdjusters.firstDayOfMonth()));
        if (account != null) {
            data.setAccountName(account.getName());
            data.setAccountNumber(account.getIdentifier());
        }
        if (account.getType() == AccountType.ASSET || account.getType() == AccountType.EXPENSE) {
            data.setCredit(BigDecimal.ZERO);
            data.setDebit(balance);
        } else {
            data.setCredit(balance);
            data.setDebit(BigDecimal.ZERO);
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

        auditTrailService.saveAuditTrail("Accounts", "Opening Account Balance for account " + identifier + " with Amount " + balance);
        return data;
    }

    public List<JournalEntryItem> openingBatchEntry(List<AccBalanceData> accs) {
//        List<JournalEntry> entries =new ArrayList();
        List<JournalEntryItem> itemArray = new ArrayList();
        for (AccBalanceData acc : accs) {
            Account account = findByAccountNumber(acc.getIdentifier()).orElse(null);

            JournalEntryItem data = new JournalEntryItem(account, "Balance b/f", BigDecimal.ZERO, acc.getBalance());
            auditTrailService.saveAuditTrail("Accounts", "Opening Account Balance for account[Batch] " + acc.getIdentifier() + " with Amount " + acc.getBalance());
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

    @Transactional
    public void updateAccountBalance(String accountNo, Boolean all) {
        log.info("updating accounts normal balances ..............................");
        if (all != null && all) {
            List<Account> lists = accountRepository.findAll();
            lists.stream().map(account -> {
                BigDecimal bal = toDefault(getAccountBalance(account.getIdentifier(), LocalDate.now()));
                account.setBalance(bal);
                return account;
            }).forEachOrdered(account -> {
                accountRepository.save(account);
                log.info("Account {} - {} New Balance: {} ", account.getIdentifier(), account.getName(), account.getBalance());
            });
            //also update ledgers

        } else if (accountNo != null) {
            Account acc = accountRepository.findByIdentifier(accountNo).orElseThrow(() -> APIException.notFound("Account with Identifier {} not found", accountNo));
            BigDecimal bal = toDefault(getAccountBalance(acc.getIdentifier(), LocalDate.now()));
            acc.setBalance(bal);
            accountRepository.save(acc);
        }
        List<Account> lists = accountRepository.findAll();

        Map<Ledger, Double> ledgerBalances = lists.stream()
                .collect(
                        Collectors.groupingBy(
                                Account::getLedger,
                                Collectors.summingDouble(x -> x.getBalance().doubleValue())
                        )
                );
        // inventory.forEach((k, v) -> {
        log.info("------------------------------------------------------------------------");
        ledgerBalances.forEach((k, v) -> {
            log.info("Ledger {} - {}  : total balance: {}", k.getIdentifier(), k.getName(), v);
        });
    }
}
