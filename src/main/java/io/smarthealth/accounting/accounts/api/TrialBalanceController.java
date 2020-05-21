package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalance;
import io.smarthealth.accounting.accounts.service.TrialBalanceService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/trialbalance")
public class TrialBalanceController {

    private final TrialBalanceService trialBalanceService;

    @Autowired
    public TrialBalanceController(final TrialBalanceService trialBalanceService) {
        super();
        this.trialBalanceService = trialBalanceService;
    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('view_trialBalance')") 
    public ResponseEntity<TrialBalance> getTrialBalance(
            @RequestParam(value = "includeEmptyEntries", required = false) final boolean includeZeroBalance) {
        return ResponseEntity.ok(this.trialBalanceService.getTrialBalance(includeZeroBalance));
    }
}
