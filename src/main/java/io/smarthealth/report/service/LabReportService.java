/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.laboratory.data.LabRegisterData;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.laboratory.service.LabConfigurationService;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.visit.service.VisitService;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class LabReportService {

    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final LaboratoryService labService;

    private final VisitService visitService;
    private final LabConfigurationService labSetUpService;

    public void getLabStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        String labNumber = reportParam.getFirst("labNumber");
        String orderNumber = reportParam.getFirst("orderNumber");
        String patientNumber = reportParam.getFirst("patientNumber");
        String search = reportParam.getFirst("search");
        String dateRange = reportParam.getFirst("dateRange");
         Boolean isWalkin = reportParam.getFirst("iswalkin")!=null?Boolean.parseBoolean(reportParam.getFirst("iswalkin")):null;
        List<LabTestStatus> status = Arrays.asList(statusToEnum(reportParam.getFirst("status")));
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Boolean expand = Boolean.parseBoolean(reportParam.getFirst("summarized"));

        List<LabRegisterData> patientData = labService.getLabRegister(labNumber, orderNumber, visitNumber, patientNumber, status, range, search, Pageable.unpaged())
                .getContent()
                .stream()
                .map((register) -> register.toData(expand))
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setPatientNumber(patientNumber);
        reportData.setFormat(format);
        if (!expand) {
            reportData.setTemplate("/clinical/laboratory/LabStatement");
        } else {
            reportData.setTemplate("/clinical/laboratory/LabStatement_summary");
        }
        reportData.setReportName("Lab-Statement");
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportService.generateReport(reportData, response);
    }

    public void getLabTestStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        String labNumber = reportParam.getFirst("labNumber");
        String orderNumber = reportParam.getFirst("orderNumber");
        String patientNumber = reportParam.getFirst("patientNumber");
        String search = reportParam.getFirst("search");
        String dateRange = reportParam.getFirst("dateRange");        
        LabTestStatus status = labService.LabTestStatusToEnum(reportParam.getFirst("status"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Boolean expand = Boolean.parseBoolean(reportParam.getFirst("summarized"));

        List<LabRegisterTestData> patientData = labService.getLabRegisterTest(labNumber, orderNumber, visitNumber, patientNumber, status, range, search, Pageable.unpaged())
                .getContent()
                .stream()
                .map((register) -> register.toData(expand))
                .collect(Collectors.toList());
        reportData.setData(patientData);
        reportData.setFormat(format);
        if (!expand) {
            reportData.setTemplate("/clinical/laboratory/LabStatement");
        } else {
            reportData.setTemplate("/clinical/laboratory/LabStatement_summary");
        }
        List<JRSortField> sortList = new ArrayList<>();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("status");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        reportData.setPatientNumber(patientNumber);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setReportName("Lab-Statement");
        reportService.generateReport(reportData, response);
    }

    public void getPatientLabReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        String labNumber = reportParam.getFirst("labNumber");
//        Visit visit = visitService.findVisitEntityOrThrow("O000005");
        LabRegisterData labTests = labService.getLabRegisterByNumber(labNumber).toData(Boolean.TRUE);//tLabResultDataByVisit(visit);
        ReportData reportData = new ReportData();
        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(labTests.getPatientNo());
        reportData.setData(Arrays.asList(labTests));
        reportData.setEmployeeId(labTests.getRequestedByStaffNumber());
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/laboratory/patientLab_report");
        reportData.setReportName("Lab-report");
        reportService.generateReport(reportData, response);
    }

    public void genSpecimenLabel(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long testId = NumberUtils.createLong(reportParam.getFirst("labRegisterTestId"));
        LabRegisterTestData testData = labService.getLabRegisterTest(testId).toData(false);

        reportData.setData(Arrays.asList(testData));
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/laboratory/specimen_label");
        reportData.setReportName("specimen-label");
        reportService.generateReport(reportData, response);
    }

    private LabTestStatus statusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(LabTestStatus.class, status)) {
            return LabTestStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Bill Status");
    }
}
