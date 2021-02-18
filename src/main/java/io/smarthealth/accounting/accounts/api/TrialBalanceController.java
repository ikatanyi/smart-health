package io.smarthealth.accounting.accounts.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.accounts.data.financial.statement.TrialBalance;
import io.smarthealth.accounting.accounts.service.TrialBalanceService;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api
@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trialbalance")
public class TrialBalanceController {

    private final TrialBalanceService trialBalanceService;
    private final AuditTrailService auditTrailService;

//    @Autowired
//    public TrialBalanceController(final TrialBalanceService trialBalanceService) {
//        super();
//        this.trialBalanceService = trialBalanceService;
//    }

    @GetMapping
    @ResponseBody
    @PreAuthorize("hasAuthority('view_trialBalance')")
    public ResponseEntity<TrialBalance> getTrialBalance(
            @RequestParam(name = "asAt", required = false) @DateTimeFormat(iso = ISO.DATE) Optional<LocalDate> localDate,
//            @RequestParam(value = "asAt", required = false) final @DateTimeFormat(pattern = Constants.DATE_PATTERN) @JsonFormat(pattern = Constants.DATE_PATTERN) LocalDate localDate,
            @RequestParam(value = "includeEmptyEntries", required = false) final boolean includeZeroBalance) {
            auditTrailService.saveAuditTrail("Trial Balance", "Viewed Trial Balance");
        return ResponseEntity.ok(this.trialBalanceService.getTrialBalance(includeZeroBalance, localDate.orElse(null)));
    }

}
