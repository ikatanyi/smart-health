/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.record.data.PatientTestsData;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class PatientReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    
    private final VisitService visitService;
    
    
    public void getPatients(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String dateRange = reportParam.getFirst("dateRange");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
        List<PatientData> patientData = (List<PatientData>) patientService.fetchAllPatients(reportParam, pageable).getContent()
                .stream()
                .map((patient) -> patientService.convertToPatientData((Patient) patient))
                .collect(Collectors.toList());
        
        reportData.setData(patientData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientList");
        reportData.setReportName("PatientList");
        reportService.generateReport(reportData, response);
    }
    
    public void getVisit(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws JRException, SQLException, IOException{
        String visitNumber = reportParam.getFirst("visitNumber");
        String staffNumber = reportParam.getFirst("staffNumber");
        String servicePointType = reportParam.getFirst("servicePointType");
        String patientNumber = reportParam.getFirst("patientNumber");
        String patientName = reportParam.getFirst("visitNumber");
        Boolean runningStaus = Boolean.getBoolean(reportParam.getFirst("visitNumber"));
        String dateRange = reportParam.getFirst("dateRange");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        ReportData reportData = new ReportData();
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
         List<VisitData> visitData = visitService.fetchAllVisits(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStaus, range, pageable)
                .stream()
                .map((visit) -> visitService.convertVisitEntityToData(visit))
                .collect(Collectors.toList());
        reportData.setData(visitData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/visit_report");
        reportData.setReportName("visit-report");
        reportService.generateReport(reportData, response);
    }
    
    public void getDiagnosis(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws JRException, SQLException, IOException{
        String visitNumber = reportParam.getFirst("visitNumber");
        String patientNumber = reportParam.getFirst("patientNumber");
        Gender gender = Gender.fromValue(reportParam.getFirst("gender"));
        String dateRange = reportParam.getFirst("dateRange");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        Integer minAge = Integer.getInteger(reportParam.getFirst("minAge"));
        Integer maxAge = Integer.getInteger(reportParam.getFirst("maxAge"));
        ReportData reportData = new ReportData();
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
         List<PatientTestsData> diagnosisData = diagnosisService.fetchAllDiagnosis(visitNumber, patientNumber, range, gender, minAge, maxAge, pageable)
                 .getContent()
                .stream()
                .map((diagnosis) -> PatientTestsData.map(diagnosis))
                .collect(Collectors.toList());
        reportData.setData(diagnosisData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/visit_report");
        reportData.setReportName("visit-report");
        reportService.generateReport(reportData, response);
    }
    
}
