package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.financial.statement.FinancialCondition;
import io.smarthealth.accounting.accounts.service.FinancialConditionService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/financialcondition")
public class FinancialConditionControllers {

    private final FinancialConditionService financialConditionService;

    public FinancialConditionControllers(final FinancialConditionService financialConditionService) {
        super();
        this.financialConditionService = financialConditionService;
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('view_financialcondition')")
    public ResponseEntity<FinancialCondition> getFinancialCondition(
            @RequestParam(name = "asAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> localDate ) {
        return ResponseEntity.ok(this.financialConditionService.getFinancialCondition(localDate.orElse(null)));
    }
}
