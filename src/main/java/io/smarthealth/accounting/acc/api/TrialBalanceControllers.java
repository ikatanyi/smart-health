package io.smarthealth.accounting.acc.api;


import io.smarthealth.accounting.acc.data.v1.financial.statement.TrialBalance;
import io.smarthealth.accounting.acc.service.TrialBalancesServices;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/trialbalance")
public class TrialBalanceControllers {

  private final TrialBalancesServices trialBalanceService;

  @Autowired
  public TrialBalanceControllers(final TrialBalancesServices trialBalanceService) {
    super();
    this.trialBalanceService = trialBalanceService;
  }

  @GetMapping
  @ResponseBody
  public ResponseEntity<TrialBalance> getTrialBalance(
      @RequestParam(value = "includeEmptyEntries", required = false) final boolean includeEmptyEntries) {
    return ResponseEntity.ok(this.trialBalanceService.getTrialBalance(includeEmptyEntries));
  }
}
