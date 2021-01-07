/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.domain.TotalTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.organization.person.service.WalkingService;
import io.smarthealth.report.data.ReportData;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class RadiologyReportService {

    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final RadiologyService scanService;
    private final BillingService billingService;
    private final VisitService visitService;
    private final WalkingService walkInService;

    public void getRadiologyStatement(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitId = reportParam.getFirst("visitNumber");
        String scanNo = reportParam.getFirst("scanNumber");
        String patientNumber = reportParam.getFirst("patientNumber");
        String dateRange = reportParam.getFirst("dateRange");
        String search = reportParam.getFirst("search");
        ScanTestState status = statusToEnum(reportParam.getFirst("status"));
        Boolean summary = Boolean.parseBoolean(reportParam.getFirst("summarized"));
        Boolean isWalkin = reportParam.getFirst("iswalkin") != null ? Boolean.parseBoolean(reportParam.getFirst("iswalkin")) : null;
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);

        List<PatientScanTestData> patientData = scanService.findAllTests(patientNumber, search, scanNo, status, visitId, range, isWalkin, Pageable.unpaged())
                .getContent()
                .stream()
                .map((register) -> {
                    PatientScanTestData data = register.toData();
                    if(data.getIsWalkin()) {
                        Optional<WalkIn> walkin = walkInService.fetchWalkingByWalkingNo(data.getPatientNumber());
                        if(walkin.isPresent())
                            data.setPatientName(walkin.get().getFullName());
                    }
                    List<PatientBillItem> billItem = billingService.getPatientBillItem(data.getReferenceNo());
                    if (!billItem.isEmpty()) {
                        data.setReferenceNo(billItem.get(0).getPaymentReference());
                        billItem.stream().map((item) -> {
                            if (data.getPaymentMode().equalsIgnoreCase("Cash")) {
                                data.setTotalCash(+item.getAmount());
                            }
                            return item;
                        }).filter((item) -> (data.getPaymentMode().equalsIgnoreCase("Insurance"))).forEachOrdered((item) -> {
                            data.setTotalInsurance(+item.getAmount());
                        });
                    }
                    return data;
                })
                .collect(Collectors.toList());
        reportData.setData(patientData);
        if (!patientData.isEmpty()) {
            reportData.setPatientNumber(patientData.get(0).getPatientNumber());
            reportData.setEmployeeId(patientData.get(0).getRequestedByStaffNumber());
        }
        if(range==null)
            range = DateRange.fromIsoStringOrReturnNull("2020-11-01..2040-11-30");
        Instant fromDate = range.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = range.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<TotalTest> tests = scanService.getPatientScansTestTotals(fromDate, toDate);
        List<TotalTest> requests = scanService.getTotalRequests(fromDate, toDate);

        reportData.setFormat(format);
        if (summary) {
            reportData.setTemplate("/clinical/radiology/radiology_statement_summary");
        } else {
            reportData.setTemplate("/clinical/radiology/radiology_statement");
        }
        reportData.getFilters().put("tests", tests);
        reportData.getFilters().put("requests", requests);
        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("status");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));

        reportData.setReportName("Radiology-Studies-Summary");
        reportService.generateReport(reportData, response);
    }


    public void getPatientRadiolgyReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Long scanTestId = NumberUtils.createLong(reportParam.getFirst("scanTestId"));
        PatientScanTestData procTests = scanService.findPatientRadiologyTestByIdWithNotFoundDetection(scanTestId).toData();//

        List<JRSortField> sortList = new ArrayList();
        JRDesignSortField sortField = new JRDesignSortField();
        reportData.setPatientNumber(procTests.getPatientNumber());
        reportData.setEmployeeId(procTests.getRequestedByStaffNumber());
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setPatientNumber(procTests.getPatientNumber());
        reportData.setData(Arrays.asList(procTests));
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
