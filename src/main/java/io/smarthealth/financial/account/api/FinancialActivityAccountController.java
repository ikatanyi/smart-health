package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.ActivityAccount;
import io.smarthealth.financial.account.data.JournalData;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.enumeration.FinancialActivity;
import io.smarthealth.financial.account.domain.FinancialActivityAccount;
import io.smarthealth.financial.account.service.FinancialActivityAccountService;
import io.smarthealth.infrastructure.exception.APIException;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.http.ActuatorMediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class FinancialActivityAccountController {

    private final FinancialActivityAccountService service;

    public FinancialActivityAccountController(FinancialActivityAccountService service) {
        this.service = service;
    }

    @PostMapping("/financialactivityaccounts")
    public ResponseEntity<?> createAccountMapping(@Valid @RequestBody ActivityAccount activityAccount) {
        FinancialActivityAccount result = service.createMapping(activityAccount);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/financialactivityaccounts/{id}")
                .buildAndExpand(result.getAccount().getIdentifier()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/financialactivityaccounts/{identifier}")
    public List<FinancialActivityAccount> getActivityMappedByAccounts(@PathVariable(value = "identifier") String identifier) {
        return service.getFinancialActivityAccount(identifier);
    }

}
