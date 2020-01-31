package io.smarthealth.accounting.acc.api;


import io.smarthealth.accounting.acc.data.SimpleAccountData;
import io.smarthealth.accounting.acc.data.v1.ChartOfAccountEntry;
import io.smarthealth.accounting.acc.service.ChartOfAccountServices;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/chartofaccounts")
public class ChartOfAccountsControllers {

  private final ChartOfAccountServices chartOfAccountsService;

  @Autowired
  public ChartOfAccountsControllers(final ChartOfAccountServices chartOfAccountsService) {
    super();
    this.chartOfAccountsService = chartOfAccountsService;
  }
 
  @GetMapping
  @ResponseBody
  public ResponseEntity<List<ChartOfAccountEntry>> getChartOfAccounts() {
    return ResponseEntity.ok(this.chartOfAccountsService.getChartOfAccounts());
  }
   
}
