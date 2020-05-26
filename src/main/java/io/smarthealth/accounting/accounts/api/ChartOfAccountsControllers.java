package io.smarthealth.accounting.accounts.api;

import io.smarthealth.accounting.accounts.data.ChartOfAccountEntry;
import io.smarthealth.accounting.accounts.service.ChartOfAccountServices;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/api/chartofaccounts")
@PreAuthorize("hasAuthority('view_chartofaccounts')")
public class ChartOfAccountsControllers {

    private final ChartOfAccountServices chartOfAccountsService;

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
