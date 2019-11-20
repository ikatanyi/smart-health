package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.AccountData;
import io.smarthealth.accounting.account.data.SimpleAccountData;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.AccountTypeRepository;
import io.smarthealth.accounting.account.domain.Journal;
import io.smarthealth.accounting.account.domain.JournalEntry;
import io.smarthealth.accounting.account.domain.JournalRepository;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.accounting.account.domain.enumeration.JournalState;
import io.smarthealth.accounting.account.domain.specification.AccountSpecification;
import io.smarthealth.accounting.account.domain.AccountsMetadata;
import io.smarthealth.infrastructure.exception.APIException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final JournalRepository journalRepository;

    public AccountService(AccountRepository accountRepository,
            AccountTypeRepository accountTypeRepository,
            JournalRepository journalRepository) {
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
        this.journalRepository = journalRepository;
    }

    public AccountData createAccount(AccountData accountData) {
        Account account = AccountData.map(accountData);
        if (accountData.getParentAccount() != null) {
            account.setParentAccount(
                    findAccount(accountData.getParentAccount())
                            .orElseThrow(() -> APIException.notFound("Reference parent account {0} not available.", accountData.getParentAccount()))
            );
        }
        AccountType accountType = accountTypeRepository.findById(accountData.getAccountType())
                .orElseThrow(() -> APIException.notFound("Account Type with Id {0} not found.", accountData.getParentAccount()));
        account.setAccountType(accountType);

        Account savedAccount = accountRepository.save(account);

        postOpeningBalance(savedAccount, accountData);

        return AccountData.map(savedAccount);
    }

    public Account findOneWithNotFoundDetection(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Account with id {0} not found", id));
    }

    public Account findOneWithNotFoundDetection(String id) {
        return accountRepository.findByAccountNumber(id)
                .orElseThrow(() -> APIException.notFound("Account with Account Number {0} not found", id));
    }

    public Page<Account> findAllAccount(Pageable page) {
        return accountRepository.findAll(page);
    }

    public Optional<Account> findAccount(String accountCode) {
        return accountRepository.findByAccountNumber(accountCode);
    }

    public String modifyAccount(AccountData accountData) {
        Account account = accountRepository.findByAccountNumber(accountData.getAccountNumber()).get();
        if (accountData.getAccountNumber() != null) {
            account.setAccountNumber(accountData.getAccountNumber());
        }
        if (accountData.getAccountName() != null) {
            account.setAccountName(accountData.getAccountName());
        }
        if (accountData.getDescription() != null) {
            account.setDescription(accountData.getDescription());
        }
        if (accountData.getAccountType() != null) {
            AccountType accountType = accountTypeRepository.findById(accountData.getAccountType())
                    .orElseThrow(() -> APIException.notFound("Account Type with Id {0} not found.", accountData.getParentAccount()));
            account.setAccountType(accountType);
        }

        Account referenceAccount = null;
        if (accountData.getParentAccount() != null) {
            if (!accountData.getParentAccount().equals(account.getParentAccount().getAccountNumber())) {
                referenceAccount = this.accountRepository.findByAccountNumber(accountData.getParentAccount()).get();
            }
        }
        account.setParentAccount(referenceAccount);

        Account updated = accountRepository.save(account);
        return updated.getAccountNumber();
    }

    public Page<Account> fetchAccounts(final boolean includeClosed, String term, final String type, final String category, boolean fetchRunningBalance, Pageable pageable) {
        AccountType accountType = null;
        if (type != null) {
            accountType = accountTypeRepository.findById(Long.valueOf(type))
                    .orElseThrow(() -> APIException.notFound("Account Type {0} not found.", type));
        }
        throwIfAccountCategoryNotValid(category);

        Specification<Account> spec = AccountSpecification.createSpecification(includeClosed, term, accountType, category);
        Page<Account> accounts = accountRepository.findAll(spec, pageable);

        if (fetchRunningBalance) {
            accounts.map(
                    acc -> {
                        acc.setBalance(BigDecimal.valueOf(732.90));
                        return acc;
                    });
        }
        return accounts;
    }

    private void postOpeningBalance(Account account, AccountData accountData) {
        //determin the rule of posting opening balances
        if (accountData.getOpeningBalance() != null && accountData.getBalanceDate() != null) {
            Journal journal = new Journal();
            journal.setTransactionId(generateTransactionId(2L));
            journal.setReversed(false);
            journal.setState(JournalState.DRAFT);
            journal.setReferenceNumber(UUID.randomUUID().toString());
            journal.setTransactionDate(accountData.getBalanceDate());
            journal.setDescriptions("Opening Balance as at " + accountData.getBalanceDate().toString());

            AccountType accountType = accountTypeRepository.findById(accountData.getAccountType()).get();
            AccountCategory category = accountType.getGlAccountType();
            JournalEntry entry;
            String desc = "Opening Balance";
            Double amount = accountData.getOpeningBalance().doubleValue();
            String entryType = "debit";
            switch (category) {
                case ASSET:
                    entryType = "debit";
                    break;
                case EQUITY:
                    entryType = "credit";
                    break;
                case EXPENSE:
                    entryType = "debit";
                    break;
                case REVENUE:
                    entryType = "credit";
                    break;
                case LIABILITY:
                    entryType = "credit";
                    break;
            }
            if (entryType.equals("debit")) {
                entry = new JournalEntry(account, 0.0D, amount, accountData.getBalanceDate(), desc);
            } else {
                entry = new JournalEntry(account, amount, 0.0D, accountData.getBalanceDate(), desc);
            }

            journal.addJournalEntry(entry);
            journalRepository.save(journal);
        }
    }

    private void throwIfAccountCategoryNotValid(String category) {
        if (category == null) {
            return;
        }
        String types = StringUtils.upperCase(category);
        try {
            AccountCategory.valueOf(types);
        } catch (Exception ex) {
            throw APIException.badRequest("Account Category : {0} is not supported .. ", category);
        }
    }

    public static String generateTransactionId(final Long companyId) {
        //journal format : ACC-JV-2019-00001
//        Long id = SecurityUtils.getCurrentLoggedUserId().get();
        final Long time = System.currentTimeMillis();
        final String uniqueVal = String.valueOf(time) + 120L + companyId;
        final String transactionId = Long.toHexString(Long.parseLong(uniqueVal));
        return transactionId;
    }

    public AccountsMetadata getAccountMetadata() {
        AccountsMetadata metadata = new AccountsMetadata();
        List<SimpleAccountData> income = accountRepository.findParentAccountIsNullAndAccountCategory(AccountCategory.REVENUE)
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        List<SimpleAccountData> expenses = accountRepository.findParentAccountIsNullAndAccountCategory(AccountCategory.EXPENSE)
                .stream()
                .map(acc -> SimpleAccountData.map(acc))
                .collect(Collectors.toList());
        metadata.setIncomeAccounts(income);
        metadata.setExpensesAccounts(expenses);

        return metadata;
    }

}

/*
Nominal Balances
Assets -Debit
Liabilities -Credit
Equity - Credit
Revenues/Income - Credit
Expenses - Debit
 */
