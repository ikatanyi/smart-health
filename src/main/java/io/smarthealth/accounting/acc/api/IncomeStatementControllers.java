package io.smarthealth.accounting.acc.api;


import io.smarthealth.accounting.acc.data.v1.financial.statement.IncomeStatement;
import io.smarthealth.accounting.acc.service.IncomesStatementService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/incomestatement")
public class IncomeStatementControllers {

  private final IncomesStatementService incomeStatementService;

  @Autowired
  public IncomeStatementControllers(final IncomesStatementService incomeStatementService) {
    super();
    this.incomeStatementService = incomeStatementService;
  }
 
  @GetMapping
  @ResponseBody
  public ResponseEntity<IncomeStatement> getIncomeStatement() {
    return ResponseEntity.ok(this.incomeStatementService.getIncomeStatement());
  }
}
