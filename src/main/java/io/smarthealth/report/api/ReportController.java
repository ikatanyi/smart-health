package io.smarthealth.report.api;

import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.provider.ReportSpecificationProvider;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.report.service.ReportService;
import io.swagger.annotations.Api;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Api
@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v2/")
@RequiredArgsConstructor
public class ReportController {

//    private final JasperReportsService reportService;
    private final ReportService reportService;

//    @GetMapping("/report")
//    public void generateReport(@RequestBody ReportData reportData, HttpServletResponse response) throws SQLException {
////         reportService.generateReport(reportData, response);
//        
//    }
    
    @PostMapping("/report/accounts/trial")
    public void generateTrialBalReport(
            @RequestBody ReportData reportData, 
            @RequestParam(value = "includeEmptyEntries", required = false) Boolean includeEmptyEntries,
            HttpServletResponse response) {
        try {
            reportService.getTrialBalance(reportData, includeEmptyEntries, response);
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    

}
