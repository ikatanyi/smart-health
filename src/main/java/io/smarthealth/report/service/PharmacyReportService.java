/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class PharmacyReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    
    private final VisitService visitService;
    
    
    public void getPrescription(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<PrescriptionData> requestData = prescriptionService.fetchAllPrescriptionsByVisit(visit, Pageable.unpaged())
                .getContent()
                .stream()
                .map((test) -> PrescriptionData.map(test))
                .collect(Collectors.toList());

        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
        }
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/pharmacy/prescription");
        reportData.setReportName("prescription");
        reportService.generateReport(reportData, response);
    }
    
    public void getPrescriptionLabel(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        PrescriptionData prescriptionData = null;
        Long prescriptionId = Long.getLong(reportParam.getFirst("prescriptionId"),null);
        Optional<Prescription> prescription = prescriptionService.fetchPrescriptionById(prescriptionId);
        if (prescription.isPresent()) {
            prescriptionData = PrescriptionData.map(prescription.get());
            reportData.setPatientNumber(prescriptionData.getPatientNumber());
        }
        reportData.setData(Arrays.asList(prescriptionData));
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/pharmacy/presc_label");
        reportData.setReportName("prescription-label");
        reportService.generateReport(reportData, response);
    }
    
}
