package io.smarthealth.accounting.accounts.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.accounts.data.financial.statement.IncomeStatement;
import io.smarthealth.accounting.accounts.service.IncomesStatementService;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import io.smarthealth.security.service.AuditTrailService;
import lombok.RequiredArgsConstructor;

@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IncomeStatementControllers {

  private final IncomesStatementService incomeStatementService;
  private final AuditTrailService auditTrailservice;

//  @Autowired
//  public IncomeStatementControllers(final IncomesStatementService incomeStatementService) {
//    super();
//    this.incomeStatementService = incomeStatementService;
//  }
 
  @GetMapping("/incomestatement")
  @ResponseBody
  @PreAuthorize("hasAuthority('view_incomestatement')")  
  public ResponseEntity<IncomeStatement> getIncomeStatement(@RequestParam(name = "asAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> localDate) {
      auditTrailservice.saveAuditTrail("Income Statement", "Viewed Income Statement");
    return ResponseEntity.ok(this.incomeStatementService.getIncomeStatement(localDate.orElse(null)));
  }
}
