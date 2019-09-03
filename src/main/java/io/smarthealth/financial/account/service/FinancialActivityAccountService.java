/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.ActivityAccount;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.enumeration.FinancialActivity;
import io.smarthealth.financial.account.domain.FinancialActivityAccount;
import io.smarthealth.financial.account.domain.FinancialActivityAccountRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class FinancialActivityAccountService {

    private final FinancialActivityAccountRepository repository;
    private final AccountRepository accountRepository;

    public FinancialActivityAccountService(FinancialActivityAccountRepository repository, AccountRepository accountRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
    }

    public FinancialActivityAccount createMapping(ActivityAccount activityAccount) {

        FinancialActivity activity = FinancialActivity.fromInt(activityAccount.getFinancialActivityId());
        Account account = accountRepository.findByIdentifier(activityAccount.getAccountIdentifier())
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", activityAccount.getAccountIdentifier()));
        
        validateActivityMapping(activity, account);
        
        FinancialActivityAccount toSave = new FinancialActivityAccount(activity.getValue(), account);
         
        return repository.save(toSave);
    }
   public List<FinancialActivityAccount> getFinancialActivityAccount(String accountNumber){
       Account account = accountRepository.findByIdentifier(accountNumber)
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", accountNumber));
       
       return repository.findByAccount(account);
   }
    private void validateActivityMapping(FinancialActivity activity, Account account) {

        if (!activity.getMappedAccountType().name().equals(account.getType())) {
            String error = "Financial Activity '" + activity.getCode() + "' with Id :" + activity.getValue()
                    + "' can only be associated with a Ledger Account of Type " + activity.getMappedAccountType().name()
                    + " the provided Ledger Account '" + account.getName() + "(" + account.getIdentifier() + ")'  does not of the required type";
            throw APIException.badRequest(error);
        }

    }
}
