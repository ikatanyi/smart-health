package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.AccountTypeRepository;
import io.smarthealth.infrastructure.exception.APIException;
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
public class AccountTypeRestController {

    private final AccountTypeRepository accountTypeRepository;

    public AccountTypeRestController(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    @PostMapping("/settings/accounts/type")
    public ResponseEntity<?> createAccountType(@Valid @RequestBody AccountType type) {
        AccountType result = accountTypeRepository.save(type);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/settings/accounts/type/{code}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(result);

    }

    @GetMapping("/settings/accounts/type/{identifier}")
    public AccountType getAccountType(@PathVariable(value = "identifier") Long identifier) {
        AccountType type = accountTypeRepository.findById(identifier)
                .orElseThrow(() -> APIException.notFound("Account Type with Identifier {0} not found.", identifier));
        return type;
    }

    @GetMapping("/settings/accounts/type")
    public ResponseEntity<List<AccountType>> getAllAccounts() {
        List<AccountType> list = accountTypeRepository.findAll();

        return ResponseEntity.ok(list);
    }
}
