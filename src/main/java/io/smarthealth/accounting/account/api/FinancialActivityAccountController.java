package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.data.ActivityAccount;
import io.smarthealth.accounting.account.domain.FinancialActivityAccount;
import io.smarthealth.accounting.account.service.FinancialActivityAccountService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Api
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
                .buildAndExpand(result.getAccount().getAccountNumber()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/financialactivityaccounts/{identifier}")
    public List<FinancialActivityAccount> getActivityMappedByAccounts(@PathVariable(value = "identifier") String identifier) {
        return service.getFinancialActivityAccount(identifier);
    }

}
