/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.acc.data.v1.financial.statement.TrialBalance;
import io.smarthealth.accounting.acc.service.TrialBalancesServices;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.data.ReportData;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class ReportService {
    private final JasperReportsService reportService;
    private final TrialBalancesServices trialBalanceService;
    
    public void getTrialBalance(ReportData reportData, final boolean includeEmptyEntries,  HttpServletResponse response) throws SQLException{
        TrialBalance trialBalance = trialBalanceService.getTrialBalance(includeEmptyEntries);
        reportData.setData(trialBalance.getTrialBalanceEntries());
        reportData.setReportName("/accounts/TrialBalance");
        reportService.generateReport(reportData, response);
    }
}
