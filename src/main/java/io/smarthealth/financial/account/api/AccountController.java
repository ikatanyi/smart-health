package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.financial.account.service.AccountService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
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
        Account account=convertToEntity(accountData);
        Account result=service.createAccount(account);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/accounts/{account_code}")
                .buildAndExpand(result.getAccountCode()).toUri();

        return ResponseEntity.created(location).body(convertToData(result));
        
    }
    
     @GetMapping("/accounts/{account_code}")
     public AccountData getAccounts(@PathVariable(value = "account_code") String code){
         Account user = service.findAccountByCode(code)
                .orElseThrow(() -> APIException.notFound("No account found for reference {0}", code));
        return convertToData(user);
     }
        @GetMapping("/accounts")
    public ResponseEntity<List<AccountData>> getAllAccounts(@RequestParam MultiValueMap<String, String> queryParams, Pageable pageable) {
        UriComponentsBuilder uriBuilder= ServletUriComponentsBuilder.fromCurrentContextPath();
         
        Page<AccountData> page = service.findAllAccount(pageable).map(u -> convertToData(u));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    public AccountData convertToData(Account account){
        AccountData data=modelMapper.map(account, AccountData.class);
        return data;
    }
    
    public Account convertToEntity(AccountData data){
        Account account=modelMapper.map(data, Account.class);
        return account;
    }
}