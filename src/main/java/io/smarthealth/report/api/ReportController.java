package io.smarthealth.report.api;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
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
            @RequestParam(value = "format", required = false) ExportFormat format, 
            @RequestParam(value = "includeEmptyEntries", required = false) Boolean includeEmptyEntries,            
            HttpServletResponse response) throws SQLException {
        reportService.getTrialBalance(includeEmptyEntries, format, response);
        
    }
    @PostMapping("/report/accounts/daily-income-statement")
    public void generateDailyIncomeStatement(
            @RequestParam(value = "format", required = false) ExportFormat format,
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "visitNo", required = false) String visitNo,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "billNo", required = false) String billNo,
            @RequestParam(value = "billStatus", required = false) String billStatus,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            HttpServletResponse response) throws SQLException {
        reportService.getDailyPayment(transactionNo, visitNo, patientNo, paymentMode, billNo, dateRange, billStatus, format, response);
        
    }
    @GetMapping("/report/accounts/daily-insurance-statement")
    public void generateDailyInsuranceStatement(
            @RequestParam(value = "format", required = false) ExportFormat format,
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "payee", required = false) Long payee,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
            @RequestParam(value = "billNo", required = false) String billNo,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            HttpServletResponse response) throws SQLException {
            reportService.genInsuranceStatement(payer, payee, invoiceNo, dateRange, patientNo, format, response);
        
        
    }
    @PostMapping("/report/accounts/invoice-statement")
    public void generateInvoiceStatement(
            @RequestParam(value = "format", required = false) ExportFormat format,
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "payee", required = false) Long payee,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
            @RequestParam(value = "billNo", required = false) String billNo,
            @RequestParam(value = "invoiceStatus", required = false) String invoiceStatus,
            @RequestParam(value = "dateRange", required = false) String dateRange, 
            HttpServletResponse response) {
        try {
            reportService.getInvoiceStatement(payer, payee, invoiceNo, patientNo, dateRange, invoiceStatus, format, response);
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @PostMapping("/report/accounts/invoice")
    public void generateInvoice(
            @RequestParam(value = "format", required = false) ExportFormat format,
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "payer", required = false) Long payer,
            @RequestParam(value = "scheme", required = false) Long scheme,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "invoiceNo", required = false) String invoiceNo,
            @RequestParam(value = "billNo", required = false) String billNo,
            @RequestParam(value = "invoiceStatus", required = false) String invoiceStatus,
            @RequestParam(value = "dateRange", required = false) String dateRange, 
            HttpServletResponse response) throws SQLException {
        reportService.getInvoice(transactionNo, payer, scheme, patientNo, invoiceNo, dateRange, invoiceStatus, format, response);
        
    }   
    
     @PostMapping("/report/patient/{patientId}/patient-file")
    public void generatePatientFile(
            @PathVariable String patientId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException {
        reportService.getPatientFile(patientId, format,response);
        
    } 
    
    @PostMapping("/report/laboratory/{visitNumber}/patient-file")
    public void generatePatientLabFile(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException {
        reportService.getPatientLabReport(visitNumber, format, response);
        
    }   
    
    @PostMapping("/report/clinical/{visitNumber}/request-form/{requestType}")
    public void generatePatientRequestFile(
            @PathVariable String visitNumber,
            @PathVariable RequestType requestType,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException {
        reportService.getPatientRequest(visitNumber, requestType, format, response);
        
    } 
    @PostMapping("/report/clinical/prescription/{visitNumber}")
    public void generatePrescription(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException {
        reportService.getPrescription(visitNumber,format, response);
        
    } 
    
    @PostMapping("/report/clinical/sick-off-note/{visitNumber}")
    public void generateSickOffNote(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException {
        reportService.getSickOff(visitNumber,format, response);
        
    } 

}
