package io.smarthealth.accounting.acc.api;

import io.smarthealth.accounting.acc.data.ActivityAccount;
import io.smarthealth.accounting.acc.data.FinancialActivityData;
import io.smarthealth.accounting.acc.domain.FinancialActivityAccount;
import io.smarthealth.accounting.acc.service.FinancialActivityAccountService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                .buildAndExpand(result.getAccount().getIdentifier()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/financialactivityaccounts/{id}")
    public FinancialActivityAccount getActivityMappedByAccounts(@PathVariable(value = "id") Long identifier) {
        return service.getActivityById(identifier);
    }

    @PutMapping("/financialactivityaccounts/{id}")
    public FinancialActivityAccount getActivityMappedByAccounts(@PathVariable(value = "id") Long identifier, @Valid @RequestBody ActivityAccount activityAccount) {
        return service.updateFinancialActivity(identifier, activityAccount);
    }

    @GetMapping("/financialactivityaccounts")
    public ResponseEntity<?> getAllFinancialActivities(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        Page<ActivityAccount> list = service.getAllFinancialMapping(pageable);

        Pager<List<ActivityAccount>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Financial Activity");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/financialActivities")
    public List<FinancialActivityData> getActivities() {
        return service.getActivities()
                .stream()
                .map(ac -> FinancialActivityData.map(ac))
                .collect(Collectors.toList());
    }

}
