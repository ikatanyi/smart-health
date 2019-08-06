package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.Transaction;
import io.smarthealth.financial.account.domain.TransactionRepository;
import io.smarthealth.financial.account.domain.specification.AccountSpecification;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.Optional;
import javax.annotation.Nullable;
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
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Page<Account> findAllAccount(Pageable page) {
        return accountRepository.findAll(page);
    }

    public Optional<Account> findAccount(String accountCode) {
        return accountRepository.findByAccountCode(accountCode);
    }

    public Page<Account> fetchAccounts(final boolean includeClosed, String term, final String type, Pageable pageable) {
        Specification<Account> spec = AccountSpecification.createSpecification(includeClosed, term, type);
        Page<Account> accounts = accountRepository.findAll(spec, pageable);
        return accounts;
    }

    public Page<Transaction> fetchTransactions(String refNumber, DateRange range, @Nullable String narration, Pageable pageable) { 
        Page<Transaction> transactions;
        if (narration != null) {
            transactions = transactionRepository.findByReferenceNoAndTransactionDateBetweenAndDescriptionContaining(refNumber, range.getStartDateTime(), range.getEndDateTime(), narration, pageable);
        } else {
            transactions = transactionRepository.findByReferenceNoAndTransactionDateBetween(refNumber, range.getStartDateTime(), range.getEndDateTime(), pageable);
        }
        return transactions;
    }
}
