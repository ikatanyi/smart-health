package io.smarthealth.accounting.accounts.service;
  
import io.smarthealth.accounting.accounts.data.ActivityAccounts;
import io.smarthealth.accounting.accounts.data.FinancialActivity;
import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccount;
import io.smarthealth.accounting.accounts.domain.FinancialActivityAccountRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.service.AuditTrailService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class FinancialActivityAccountService {

    private final FinancialActivityAccountRepository repository;
    private final AccountService accountServices;
    private final AuditTrailService auditTrailService;

    public FinancialActivityAccount createMapping(ActivityAccounts activityAccount) {

        Optional<FinancialActivityAccount> fac = repository.findByFinancialActivity(activityAccount.getActivity());
        if (fac.isPresent()) {
            throw APIException.badRequest("Financial Activity {0} is already mapped", activityAccount.getActivity().name());
        }

        Account account = accountServices.findByAccountNumber(activityAccount.getAccountIdentifier())
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", activityAccount.getAccountIdentifier()));
        FinancialActivity activity = activityAccount.getActivity();
        validateActivityMapping(activity, account);
        auditTrailService.saveAuditTrail("Accounts", "created account mapping activity for "+  activityAccount.getActivity().name());
        return repository.save(new FinancialActivityAccount(activity, account));
    }

    public Page<ActivityAccounts> getAllFinancialMapping(Pageable page) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed All Financial Mappings ");
        return repository.findAll(page)
                .map(acc -> ActivityAccounts.map(acc));

    }

    public FinancialActivityAccount updateFinancialActivity(Long id, ActivityAccounts activity) {
        FinancialActivityAccount fa = getActivityById(id);
        Account account = accountServices.findByAccountNumber(activity.getAccountIdentifier())
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", activity.getAccountIdentifier()));

        if (fa.getAccount() != account) {
            fa.setAccount(account);
        }
        auditTrailService.saveAuditTrail("Accounts", "Updated Fincial Activity account for "+activity.getActivityName()+" to "+ account);
        return repository.save(fa);
    }

    public FinancialActivityAccount getActivityById(Long id) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account identified by id "+id);
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Activity with Id {0} not found", id));
    }

    public FinancialActivityAccount getActivityByAccount(String accountNumber) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account identified by AccountNo. "+accountNumber);
        return getFinancialActivityAccount(accountNumber)
                .orElseThrow(() -> APIException.notFound("Activity with Id {0} not found", accountNumber));
    }

    public Optional<FinancialActivityAccount> getByTransactionType(FinancialActivity activity) {
        return repository.findByFinancialActivity(activity);

    }

    public Optional<FinancialActivityAccount> getFinancialActivityAccount(String accountNumber) {
        auditTrailService.saveAuditTrail("Accounts", "Viewed Account identified by AccountNo. "+accountNumber);
        Account account = accountServices.findByAccountNumber(accountNumber)
                .orElseThrow(() -> APIException.notFound("Account {0} Not Found", accountNumber));

        return repository.findByAccount(account);
    }

    private void validateActivityMapping(FinancialActivity activity, Account account) {
        if (!activity.getAccountType().equals(account.getLedger().getAccountType())) {
            String error = "Financial Activity {0}  can only be associated with a Ledger Account of Type {1} the provided Ledger Account {2} ({3})'  is not of the required type";
            throw APIException.badRequest(error, activity.getActivityName(), activity.getAccountType().name(), account.getName(), account.getIdentifier());
        }

    }

    public List<FinancialActivity> getActivities() {
        auditTrailService.saveAuditTrail("Accounts", "Viewed financial Activities ");
        return Arrays.asList(FinancialActivity.values());
    }
}
