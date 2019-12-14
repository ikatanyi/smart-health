package io.smarthealth.accounting.acc.api;

 
import io.smarthealth.accounting.acc.data.v1.financial.statement.FinancialCondition;
import io.smarthealth.accounting.acc.service.FinancialConditionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/financialcondition")
public class FinancialConditionControllers {

  private final FinancialConditionService financialConditionService;

  @Autowired
  public FinancialConditionControllers(final FinancialConditionService financialConditionService) {
    super();
    this.financialConditionService = financialConditionService;
  }
 
  @GetMapping
  @ResponseBody
  public ResponseEntity<FinancialCondition> getFinancialCondition() {
    return ResponseEntity.ok(this.financialConditionService.getFinancialCondition());
  }
}
