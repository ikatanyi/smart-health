/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.api;

import io.smarthealth.financial.account.data.IncomeStatement;
import io.smarthealth.financial.account.service.IncomeStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api/incomestatement")
public class IncomeStatementController {

  private final IncomeStatementService incomeStatementService;
 
  public IncomeStatementController(final IncomeStatementService incomeStatementService) {
    super();
    this.incomeStatementService = incomeStatementService;
  }
  @GetMapping
  @ResponseBody
  public ResponseEntity<IncomeStatement> getIncomeStatement() {
    return ResponseEntity.ok(this.incomeStatementService.getIncomeStatement());
  }
}
