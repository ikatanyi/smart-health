/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.design.JRDesignSortField;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class RadiologyReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final RadiologyService scanService;
    
    private final VisitService visitService;
    
    
    public void getRadiologyStatement(MultiValueMap<String, String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitId = reportParam.getFirst("visitNumber");
        String scanNo = reportParam.getFirst("scanNumber");
        String orderNumber = reportParam.getFirst("orderNumber");
        String patientNumber = reportParam.getFirst("patientNumber");
        String dateRange = reportParam.getFirst("dateRange");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        ScanTestState status = statusToEnum(reportParam.getFirst("status"));
        Boolean summary = Boolean.parseBoolean(reportParam.getFirst("summarized"));
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
         List<PatientScanRegisterData> patientData = scanService.findAll(patientNumber, scanNo, visitId, status, range, pageable)
                .getContent()
                .stream()
                .map((register) -> register.todata())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        if(summary)
             reportData.setTemplate("/clinical/radiology/radiology_statement_summary");
            else
              reportData.setTemplate("/clinical/radiology/radiology_statement");
        reportData.setReportName("Lab-Statement");
        reportService.generateReport(reportData, response);
    }    
    
    public void getPatientRadiolgyReport(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientScanRegisterData> procTests = scanService.findPatientScanRegisterByVisit(visit)
                .stream()
                .map((test) -> test.todata())
                .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(procTests);
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
        }
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/procedure/patient_procedure_report");
        reportData.setReportName("procedure-report");
        reportService.generateReport(reportData, response);
    }
    
    private ScanTestState statusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(ScanTestState.class, status)) {
            return ScanTestState.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
}
