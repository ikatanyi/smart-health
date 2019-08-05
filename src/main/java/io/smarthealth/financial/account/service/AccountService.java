package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.Transaction;
import  io.smarthealth.financial.account.domain.specification.AccountSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class AccountService {
     private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
     public Account createAccount(Account account){
         //Todo:: add the account generations in the event no code is provided
         //
         return accountRepository.save(account);
     }
     public Page<Account> findAllAccount(Pageable page){
         return accountRepository.findAll(page);
     }
     
     public Optional<Account> findAccount(String accountCode){
         return accountRepository.findByCode(accountCode);
     }
     public Page<Account> fetchAccounts(final boolean includeClosed, String term, final String type, Pageable pageable){
         Page<Account> accounts=accountRepository.findAll(AccountSpecification.createSpecification(includeClosed, term, type),  pageable);
         return accounts;
     }
     //find tranasctions by account
     public Page<Transaction> fetchTransactions(String accountCode, DateRange range,@Nullable String message, Pageable pageable){
         Account account=accountRepository.findByCode(accountCode).orElseThrow(()-> APIException.notFound("Acc", args))
     }
}
