package io.smarthealth.report.api;

import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.report.domain.enumeration.ReportName;
import io.smarthealth.report.service.LabReportService;
import io.smarthealth.report.service.PatientReportService;
import io.smarthealth.report.service.RadiologyReportService;
import io.smarthealth.report.service.ReportService;
import io.swagger.annotations.Api;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.util.MultiValueMap;
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
    private final LabReportService labReportService;
    private final RadiologyReportService radiologyReportService;
    private final PatientReportService patientReportService;
    
    
    @GetMapping("/report")
    public void generateReport(
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
                reportService.getPatientFile(queryParams, format, response);
                break;
            case Lab_Report:
                reportService.getPatientLabReport(queryParams, format, response);
                break;
            case Procedure_Report:
                reportService.getPatientProcedureReport(queryParams, format, response);
                break;
            case Request_Form:
                 reportService.getPatientRequest(queryParams, format, response);
                break;
            case Prescription:
                reportService.getPrescription(queryParams, format, response);
                break;
            case Radiology_Report:
                reportService.getPatientRadiologyReport(queryParams, format, response);
                break;
            case Sick_Off_Note:
                reportService.getSickOff(queryParams, format, response);
                break;
            case Specimen_Label:
                reportService.genSpecimenLabel(queryParams, format, response);
                break;
            case Prescription_Label:
                reportService.getPrescriptionLabel(queryParams, format, response);
                break;
            case Lab_Statement_Summarized:
                labReportService.getLabStatement(queryParams, format, response);
                break;
            case Lab_Statement:
                labReportService.getLabStatement(queryParams, format, response);
                break;
            case Radiology_Statement:
                radiologyReportService.getRadiologyStatement(queryParams, format, response);
                break;
            case Radiology_Statement_Summarized:
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
            default:
                break;

        }

    }

    

    @GetMapping("/report/clinical/prescription-label/{prescriptionId}")
    public void generatePrescriptionLabel(
            @PathVariable Long prescriptionId,
            @RequestParam(value = "format", required = false) ExportFormat format,
            HttpServletResponse response) throws SQLException, JRException, IOException {
        

    }

}
