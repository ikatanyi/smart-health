/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.procedure.service.ProcedureService;
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
public class ProcedureReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final ProcedureService procedureService;
    
    private final VisitService visitService;
    
    
   public void getPatientProcedureReport(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String PatientNumber= reportParam.getFirst("PatientNumber");
        String scanNo=reportParam.getFirst("scanNo"); 
        String visitNumber=reportParam.getFirst("visitNumber");
        ProcedureTestState status=statusToEnum(reportParam.getFirst("status"));
        String dateRange=reportParam.getFirst("range");
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        List<PatientProcedureTestData> procTests = procedureService.findPatientProcedureTests(PatientNumber, scanNo, visitNumber, status, range, Pageable.unpaged())
                .stream()
                .map((test) -> test.toData())
                .collect(Collectors.toList());

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("procedureDate");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(procTests);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/procedure/procedure_statement");
        reportData.setReportName("procedure-report");
        reportService.generateReport(reportData, response);
    }
   
   private ProcedureTestState statusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(ProcedureTestState.class, status)) {
            return ProcedureTestState.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
    
}
