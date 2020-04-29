package io.smarthealth.report.api;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.report.domain.enumeration.ReportName;
import io.smarthealth.report.service.LabReportService;
import io.smarthealth.report.service.PatientReportService;
import io.smarthealth.report.service.RadiologyReportService;
import io.smarthealth.report.service.AccountReportService;
import io.smarthealth.report.service.PharmacyReportService;
import io.smarthealth.report.service.ProcedureReportService;
import io.smarthealth.report.service.SupplierReportService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@Api
@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class ReportController {

//    private final JasperReportsService reportService;
    private final AccountReportService reportService;
    private final LabReportService labReportService;
    private final RadiologyReportService radiologyReportService;
    private final PatientReportService patientReportService;
    private final SupplierReportService supplierInvoiceService;
    private final ProcedureReportService procedureReportService;
    private final PharmacyReportService pharmacyReportService;
    
    
    @GetMapping("/report")
    public ResponseEntity<?> generateReport(
            @RequestParam(value = "reportName", required = true) ReportName reportName,
            @RequestParam(required = false) MultiValueMap<String, String> queryParams,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        switch (reportName) {
            case Trial_Balance:
                reportService.getTrialBalance(queryParams, format, response);
                break;
            case Daily_Income_Statement:
                reportService.getDailyPayment(queryParams, format, response);
                break;
            case Insurance_Statement:
                reportService.genInsuranceStatement(queryParams, format, response);
                break;
            case Invoice_Statement:
                reportService.getInvoiceStatement(queryParams, format, response);
                break;
            case Invoice:
                reportService.getInvoice(queryParams, format, response);
                break;
            case Patient_file:
                patientReportService.getPatientFile(queryParams, format, response);
                break;
            case Lab_Report:
                labReportService.getPatientLabReport(queryParams, format, response);
                break;
            case Procedure_Report:
                procedureReportService.getPatientProcedureReport(queryParams, format, response);
                break;
            case Request_Form:
                 patientReportService.getPatientRequest(queryParams, format, response);
                break;
            case Prescription:
                pharmacyReportService.getPrescription(queryParams, format, response);
                break;
            case Radiology_Report:
                radiologyReportService.getPatientRadiolgyReport(queryParams, format, response);
                break;
            case Sick_Off_Note:
                patientReportService.getSickOff(queryParams, format, response);
                break;
            case Specimen_Label:
                labReportService.genSpecimenLabel(queryParams, format, response);
                break;
            case Prescription_Label:
                pharmacyReportService.getPrescriptionLabel(queryParams, format, response);
                break;
            case Lab_Statement_Summarized:
                labReportService.getLabTestStatement(queryParams, format, response);
                break;
            case Lab_Statement:
                labReportService.getLabTestStatement(queryParams, format, response);
                break;
            case Radiology_Statement:
                radiologyReportService.getRadiologyStatement(queryParams, format, response);
                break;
            case Patient_List:
                patientReportService.getPatients(queryParams, format, response);
                break;
            case Patient_Card:
                patientReportService.getPatientCard(queryParams, format, response);
                break;    
            case Patient_Visit:
                patientReportService.getVisit(queryParams, format, response);
                break;
            case Patient_Diagnosis:
                patientReportService.getDiagnosis(queryParams, format, response);
                break;
            case Doctor_Invoice:
                supplierInvoiceService.getDoctorInvoiceStatement(queryParams, format, response);
                break;
            case Supplier_Invoice:
                supplierInvoiceService.SupplierInvoiceStatement(queryParams, format, response);
                break;
            case Patient_Receipt:
                reportService.getPatientReceipt(queryParams, format, response);
                break;
            case Accounts:
                reportService.getAccounts(queryParams, format, response);
                break;
            case Income_Expense_Account:
                reportService.getIncomeExpenseAccounts(format, response);
                break;
            case Chart_Of_Account:
                reportService.getChartOfAccounts(format, response);
                break; 
            case Balance_Sheet:
                reportService.getBalanceSheet(format, response);
            case Income_Statement:
                reportService.getIncomeStatement(format, response);
                break;
            default:
                break;

        }
         return ResponseEntity.ok(""); 
    }

    

    @GetMapping("/report/clinical/prescription-label/{prescriptionId}")
    public void generatePrescriptionLabel(
            @PathVariable Long prescriptionId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        

    }

}
