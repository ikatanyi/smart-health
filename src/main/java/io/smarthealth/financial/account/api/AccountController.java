package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountCommand;
import static io.smarthealth.financial.account.data.AccountCommand.Action.CLOSE;
import static io.smarthealth.financial.account.data.AccountCommand.Action.REOPEN;
import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.data.ChildAccount;
import io.smarthealth.financial.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.financial.account.service.AccountService;
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
public class AccountController {

    private final AccountService service;
    private final ModelMapper modelMapper;

    public AccountController(AccountService accountService, ModelMapper modelMapper) {
        this.service = accountService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountData accountData) {

        if (service.findAccount(accountData.getAccountCode()).isPresent()) {
            throw APIException.conflict("Account {0} already exists.", accountData.getAccountCode());
        }
        Account account = convertToEntity(accountData);
        if (accountData.getParentAccount() != null) {
            account.setParent(
                    service.findAccount(accountData.getParentAccount())
                            .orElseThrow(() -> APIException.notFound("Reference parent account {0} not available.", accountData.getParentAccount()))
            );
        }

        Account result = service.createAccount(account);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/accounts/{code}")
                .buildAndExpand(result.getAccountCode()).toUri();

        return ResponseEntity.created(location).body(convertToData(result));

    }

    @GetMapping("/accounts/{code}")
    public AccountData getAccounts(@PathVariable(value = "code") String code) {
        Account user = service.findAccount(code)
                .orElseThrow(() -> APIException.notFound("Account {0} not found.", code));
        return convertToData(user);
    }

    @PutMapping("/accounts/{code}")
    @ResponseBody
    public ResponseEntity<Void> modifyAccount(@PathVariable("code") final String accountCode, @RequestBody @Valid final AccountData account) {
        if (!accountCode.equals(account.getAccountCode())) {
            throw APIException.badRequest("Addressed resource {0} does not match account {1}", accountCode, account.getAccountCode());
        }
        if (!this.service.findAccount(accountCode).isPresent()) {
            throw APIException.notFound("Account {0} not found.", accountCode);
        }
        if (account.getParentAccount() != null
                && !this.service.findAccount(account.getParentAccount()).isPresent()) {
            throw APIException.badRequest("Reference Parent account {0} not available.", account.getParentAccount());
        }
        service.modifyAccount(account);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountData>> getAllAccounts(
            @RequestParam(value = "includeClosed", required = false, defaultValue = "false") final boolean includeClosed,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "type", required = false) final String type, Pageable pageable) {

        Page<AccountData> page = service.fetchAccounts(includeClosed, term, type, pageable).map(u -> convertToData(u));

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
                    if (account.getAccountBalance().getBalance() != 0.00D) {
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

    public AccountData convertToData(Account account) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        AccountData data = modelMapper.map(account, AccountData.class);
        List<ChildAccount> childrenAccounts = new ArrayList<>();
        account.getChildren().forEach((child) -> {
            childrenAccounts.add(
                    new ChildAccount(child.getAccountCode(), child.getAccountName(), child.getAccountType())
            );
        });

        data.setChildren(childrenAccounts);
        return data;
    }

    public Account convertToEntity(AccountData data) {
        Account account = modelMapper.map(data, Account.class);
        return account;
    }
}
