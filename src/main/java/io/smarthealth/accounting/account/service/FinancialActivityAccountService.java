package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.data.ActivityAccount;
import io.smarthealth.accounting.account.domain.Account;
import io.smarthealth.accounting.account.domain.AccountRepository;
import io.smarthealth.accounting.account.domain.enumeration.FinancialActivity;
import io.smarthealth.accounting.account.domain.FinancialActivityAccount;
import io.smarthealth.accounting.account.domain.FinancialActivityAccountRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Account account = accountRepository.findByAccountNumber(activityAccount.getAccountIdentifier())
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", activityAccount.getAccountIdentifier()));
        FinancialActivity activity = activityAccount.getActivity();
        validateActivityMapping(activity, account);

        return repository.save(new FinancialActivityAccount(activity, account));
    }

    public Page<ActivityAccount> getAllFinancialMapping(Pageable page) {
        return repository.findAll(page)
                .map(acc -> ActivityAccount.map(acc));

    }

    public FinancialActivityAccount updateFinancialActivity(Long id, ActivityAccount activity) {
        FinancialActivityAccount fa = getActivityById(id);
        Account account = accountRepository.findByAccountNumber(activity.getAccountIdentifier())
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", activity.getAccountIdentifier()));

        if (fa.getAccount() != account) {
            fa.setAccount(account);
        }
        return repository.save(fa);
    }

    public FinancialActivityAccount getActivityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Activity with Id {0} not found", id));
    }

    public FinancialActivityAccount getActivityByAccount(String accountNumber) {
        return getFinancialActivityAccount(accountNumber)
                .orElseThrow(() -> APIException.notFound("Activity with Id {0} not found", accountNumber));
    }

    public Optional<FinancialActivityAccount> getByTransactionType(FinancialActivity activity) {
        return repository.findByFinancialActivity(activity);
                
    }

    public Optional<FinancialActivityAccount> getFinancialActivityAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", accountNumber));

        return repository.findByAccount(account);
    }

    private void validateActivityMapping(FinancialActivity activity, Account account) {

        if (activity.getCategory() != account.getAccountType().getGlAccountType()) {
            String error = "Financial Activity {0}  can only be associated with a Ledger Account of Type {1} the provided Ledger Account {2} ({3})'  is not of the required type";
            throw APIException.badRequest(error, activity.getActivityName(), activity.getCategory().name(), account.getAccountName(), account.getAccountNumber());
        }

    }

    public List<FinancialActivity> getActivities() {
        return Arrays.asList(FinancialActivity.values());
    }
}
