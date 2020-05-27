package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatement;
import io.smarthealth.accounting.accounts.service.IncomesStatementService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api")
public class IncomeStatementControllers {

  private final IncomesStatementService incomeStatementService;

  @Autowired
  public IncomeStatementControllers(final IncomesStatementService incomeStatementService) {
    super();
    this.incomeStatementService = incomeStatementService;
  }
 
  @GetMapping("/incomestatement")
  @ResponseBody
  @PreAuthorize("hasAuthority('view_incomestatement')")  
  public ResponseEntity<IncomeStatement> getIncomeStatement() {
    return ResponseEntity.ok(this.incomeStatementService.getIncomeStatement());
  }
}
