package io.smarthealth.report.api;

import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.report.service.ReportService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
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
    
    @GetMapping("/report/accounts/trial")
    public void generateTrialBalReport(
            @RequestParam(value = "format", required = false) ExportFormat format, 
            @RequestParam(value = "includeEmptyEntries", required = false) Boolean includeEmptyEntries,            
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getTrialBalance(includeEmptyEntries, format, response);
        
    }
    @GetMapping("/report/accounts/daily-income-statement")
    public void generateDailyIncomeStatement(
            @RequestParam(value = "format", required = false) ExportFormat format,
            @RequestParam(value = "transactionNo", required = false) String transactionNo,
            @RequestParam(value = "visitNo", required = false) String visitNo,
            @RequestParam(value = "patientNo", required = false) String patientNo,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "billNo", required = false) String billNo,
            @RequestParam(value = "billStatus", required = false) String billStatus,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            HttpServletResponse response) throws SQLException, JRException, IOException {
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
            HttpServletResponse response) throws SQLException, IOException, JRException {
            reportService.genInsuranceStatement(payer, payee, invoiceNo, dateRange, patientNo, format, response);
        
        
    }
    @GetMapping("/report/accounts/invoice-statement")
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
            HttpServletResponse response) throws JRException, IOException {
        try {
            reportService.getInvoiceStatement(payer, payee, invoiceNo, patientNo, dateRange, invoiceStatus, format, response);
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @GetMapping("/report/accounts/invoice")
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
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getInvoice(transactionNo, payer, scheme, patientNo, invoiceNo, dateRange, invoiceStatus, format, response);
        
    }   
    
     @GetMapping("/report/patient/{patientId}/patient-file")
    public void generatePatientFile(
            @PathVariable String patientId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPatientFile(patientId, format,response);
        
    } 
    
    @GetMapping("/report/laboratory/{visitNumber}/lab-report")
    public void generatePatientLabFile(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPatientLabReport(visitNumber, format, response);
        
    }   
    
    @GetMapping("/report/clinical/{visitNumber}/request-form/{requestType}")
    public void generatePatientRequestFile(
            @PathVariable String visitNumber,
            @PathVariable RequestType requestType,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPatientRequest(visitNumber, requestType, format, response);
        
    } 
    @GetMapping("/report/clinical/prescription/{visitNumber}")
    public void generatePrescription(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPrescription(visitNumber,format, response);
        
    } 
    @GetMapping("/report/clinical/procedure-report/{visitNumber}")
    public void generateProcedureReport(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPatientProcedureReport(visitNumber,format, response);
        
    } 
    @GetMapping("/report/clinical/radiology-report/{visitNumber}")
    public void generateRadiologyReport(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPatientRadiologyReport(visitNumber,format, response);
        
    } 
    
    @GetMapping("/report/clinical/sick-off-note/{visitNumber}")
    public void generateSickOffNote(
            @PathVariable String visitNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getSickOff(visitNumber,format, response);
        
    } 
    
    @GetMapping("/report/clinical/specimen-label/{specimenId}/patient/{patientNumber}")
    public void generateSpecimenLabel(
            @PathVariable Long specimenId,
            @PathVariable String patientNumber,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.genSpecimenLabel(patientNumber, specimenId, format, response);
        
    } 
    
    @GetMapping("/report/clinical/prescription-label/{prescriptionId}")
    public void generatePrescriptionLabel(
            @PathVariable Long prescriptionId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        reportService.getPrescriptionLabel(prescriptionId, format, response);
        
    } 

}
