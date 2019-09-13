/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.data.TrialBalance;
import io.smarthealth.accounting.account.service.TrialBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */ 
@Slf4j
@RestController
@RequestMapping("/api/trialbalance")
public class TrialBalanceController {

  private final TrialBalanceService trialBalanceService;
 
  public TrialBalanceController(final TrialBalanceService trialBalanceService) {
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
