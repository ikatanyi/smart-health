package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.data.AccountTypeData;
import io.smarthealth.accounting.account.service.AccountService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@Slf4j
@RequestMapping("/api")
public class AccountTypeRestController {

    private final AccountService service;

    public AccountTypeRestController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/account-types")
    public ResponseEntity<?> createAccountType(@Valid @RequestBody AccountTypeData data) {

        AccountTypeData result = service.createAccountType(data);

        Pager<AccountTypeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success Created");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/account-types/{id}")
    public ResponseEntity<?> getAccountType(@PathVariable(value = "id") Long id) {
        AccountTypeData data = service.getAccountType(id).toData();
        return ResponseEntity.ok(data);
    }

    @PutMapping("/account-types/{id}")
    public ResponseEntity<?> updateAccountType(@PathVariable(value = "id") Long id, AccountTypeData data) {
        AccountTypeData result = service.updateAccountType(id, data);

        Pager<AccountTypeData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Service Point Success updated");
        pagers.setContent(result);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pagers);
    }

    @GetMapping("/account-types")
    public ResponseEntity<?> listAccountType(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "1000", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<AccountTypeData> list = service.listAccountTypes(pageable);
        Pager<List<AccountTypeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Service points");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
