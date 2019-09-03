package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountCommand;
import static io.smarthealth.financial.account.data.AccountCommand.Action.CLOSE;
import static io.smarthealth.financial.account.data.AccountCommand.Action.REOPEN;
import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.ChartOfAccountEntry; 
import io.smarthealth.financial.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.financial.account.service.AccountService;
import io.smarthealth.financial.account.service.ChartOfAccountsService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class AccountRestController {

    private final AccountService service; 
    private final ChartOfAccountsService chartOfAccountsService;

    public AccountRestController(AccountService accountService, ChartOfAccountsService chartOfAccountsService) {
        this.service = accountService; 
        this.chartOfAccountsService = chartOfAccountsService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountData accountData) {
        if (service.findAccount(accountData.getIdentifier()).isPresent()) {
            throw APIException.conflict("Account {0} already exists.", accountData.getIdentifier());
        }
        
       
        AccountData result = service.createAccount(accountData);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/accounts/{code}")
                .buildAndExpand(result.getIdentifier()).toUri();

        return ResponseEntity.created(location).body(result);

    }

    @GetMapping("/accounts/{code}")
    public AccountData getAccounts(@PathVariable(value = "code") String code) {
        Account user = service.findAccount(code)
                .orElseThrow(() -> APIException.notFound("Account {0} not found.", code));
        return service.convertToData(user);
    }

    @PutMapping("/accounts/{code}")
    @ResponseBody
    public ResponseEntity<Void> modifyAccount(@PathVariable("code") final String accountCode, @RequestBody @Valid final AccountData accountData) {
        if (!accountCode.equals(accountData.getIdentifier())) {
            throw APIException.badRequest("Addressed resource {0} does not match account {1}", accountCode, accountData.getIdentifier());
        }
        if (!this.service.findAccount(accountCode).isPresent()) {
            throw APIException.notFound("Account {0} not found.", accountCode);
        }
        if (accountData.getReferenceAccount() != null
                && !this.service.findAccount(accountData.getReferenceAccount()).isPresent()) {
            throw APIException.badRequest("Reference Parent account {0} not available.", accountData.getReferenceAccount());
        }
        service.modifyAccount(accountData);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountData>> getAllAccounts(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "fetchRunningBalance", required = false) final boolean runningBalance,
            @RequestParam(value = "type", required = false) final String type, Pageable pageable) {

        Page<AccountData> page = service.fetchAccounts(includeClosed, term, type, pageable).map(u -> service.convertToData(u));

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("size", String.valueOf(page.getSize()));
        queryParams.add("page", String.valueOf(page.getNumber()));

        if (term != null) {
            queryParams.add("term", term);
        }
        if (includeClosed == true) {
            queryParams.add("includeClosed", String.valueOf(includeClosed));
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(queryParams, page, "/api/accounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/accounts/{code}/commands")
    @ResponseBody
    ResponseEntity<Void> accountCommand(@PathVariable("code") final String identifier, @RequestBody @Valid final AccountCommand accountCommand) {
        final Optional<Account> optionalAccount = this.service.findAccount(identifier);
        if (optionalAccount.isPresent()) {
            final Account account = optionalAccount.get();

            switch (accountCommand.getAction()) {
                case CLOSE:
                    if (account.getBalance() != 0.00D) {
                        throw APIException.conflict("Account {0} has remaining balance.", identifier);
                    }
                    System.err.println("Closing an account... ");
                    break;
                case REOPEN:
                    System.err.println("Reopening an account ...");
                    break;
                default:
                    throw APIException.badRequest("Invalid state change.");
            }
            return ResponseEntity.accepted().build();
        } else {
            throw APIException.notFound("Account {0} not found.", identifier);
        }
    }

    @GetMapping("/chartofaccounts")
    @ResponseBody
    public ResponseEntity<List<ChartOfAccountEntry>> getChartOfAccounts() {
        return ResponseEntity.ok(this.chartOfAccountsService.getChartOfAccounts());
    }

    
}
