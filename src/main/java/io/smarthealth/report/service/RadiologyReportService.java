/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
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
import java.util.Arrays;
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
        String search = reportParam.getFirst("search");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        ScanTestState status = statusToEnum(reportParam.getFirst("status"));
        Boolean summary = Boolean.parseBoolean(reportParam.getFirst("summarized"));
        Boolean isWalkin = Boolean.parseBoolean(reportParam.getFirst("iswalkin"));
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
         List<PatientScanTestData> patientData = scanService.findAllTests(patientNumber, search, scanNo, status, visitId, range, isWalkin, pageable)
                .getContent()
                .stream()
                .map((register) -> register.toData())
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        if(summary)
             reportData.setTemplate("/clinical/radiology/radiology_statement_summary");
            else
              reportData.setTemplate("/clinical/radiology/radiology_statement");
        
        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("status");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        
        reportData.setReportName("Radiology-Statement");
        reportService.generateReport(reportData, response);
    }    
    
    public void getPatientRadiolgyReport(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String accessNumber = reportParam.getFirst("scanNumber");
       PatientScanRegisterData procTests = scanService.findPatientRadiologyTestByAccessNoWithNotFoundDetection(accessNumber).todata();
             
        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(procTests.getPatientNumber());
        reportData.setData(Arrays.asList(procTests));
        reportData.setEmployeeId(procTests.getRequestedById());
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/radiology/patient_radiology_report");
        reportData.setReportName("Patient-scan-report");
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
