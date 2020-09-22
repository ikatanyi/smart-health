/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.moh.data.Register;
import io.smarthealth.clinical.pharmacy.data.DispensedDrugData;
import io.smarthealth.clinical.pharmacy.domain.DispensedDrugsInterface;
import io.smarthealth.clinical.pharmacy.service.DispensingService;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.lang.DateRange;
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
public class PharmacyReportService {
    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final DispensingService dispenseService;
    
    private final VisitService visitService;
    
    
    public void getPrescription(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Visit visit = null;
        String visitNumber = reportParam.getFirst("visitNumber");
        String orderNumber = reportParam.getFirst("orderNumber");
        Optional<Visit> visit1 = visitService.findVisit(visitNumber);
        if(visit1.isPresent()){
            visit = visit1.get();
            reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        }
        List<PrescriptionData> requestData = prescriptionService.fetchPrescriptionByNumber(orderNumber, visit)
//                .getContent()
                .stream()
                .map((test) -> PrescriptionData.map(test))
                .collect(Collectors.toList());

        
        
//        if (visit.getHealthProvider() != null) {
//            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
//        }
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/pharmacy/prescription");
        reportData.setReportName("prescription");
        reportService.generateReport(reportData, response);
    }
    
    public void getPrescriptionLabel(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        PrescriptionData prescriptionData = null;
        Long prescriptionId = NumberUtils.createLong(reportParam.getFirst("prescriptionId"));
        Optional<Prescription> prescription = prescriptionService.fetchPrescriptionById(prescriptionId);
        if (prescription.isPresent()) {
            prescriptionData = PrescriptionData.map(prescription.get());
            reportData.setPatientNumber(prescriptionData.getPatientNumber());
            if(prescriptionData.getPatientData()!=null)
               reportData.getFilters().put("age", prescriptionData.getPatientData().getAge());
            else
               reportData.getFilters().put("age", "");
        }
        if(prescriptionData.getItemType().equalsIgnoreCase("tablet")||prescriptionData.getItemType().equalsIgnoreCase("capsule"))
            reportData.getFilters().put("type", "TABLET/CAPSULES");
        if(prescriptionData.getItemType().equalsIgnoreCase("syrup"))
            reportData.getFilters().put("type", "SYRUP/SUSPENSION");
        else
            reportData.getFilters().put("type", "PESSARIES/SUPPOSITORIES");
        reportData.setData(Arrays.asList(prescriptionData));
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/pharmacy/presc_label");
        reportData.setReportName("prescription-label");
        reportService.generateReport(reportData, response);
    }
    
    public void DispenseReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        DateRange range = DateRange.fromIsoString(reportParam.getFirst("dateRange"));
        List<DispensedDrugsInterface> requestData = dispenseService.dispensedDrugs(range);
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setTemplate("/clinical/pharmacy/dispensed_drugs");
        reportData.setReportName("Dispensed-Drugs");
        reportService.generateReport(reportData, response);
    }
    
    public void getPatientDispensedDrugs(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String transactionNo= reportParam.getFirst("transactionNo");
        String visitNo = reportParam.getFirst("visitNo");
        String patientNo = reportParam.getFirst("patientNo");
        String prescriptionNo = reportParam.getFirst("prescriptionNo");
        String billNo= reportParam.getFirst("billNo");
        Boolean isReturn=null;
        reportData.setPatientNumber(patientNo);
        List<DispensedDrugData> requestData = dispenseService.findDispensedDrugs(transactionNo, visitNo, patientNo, prescriptionNo, billNo, isReturn, Pageable.unpaged())
                .getContent()
                .stream()
                .map(drug -> drug.toData())
                .collect(Collectors.toList());

        
        
//        if (visit.getHealthProvider() != null) {
//            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
//        }
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/clinical/pharmacy/Patient_dispensed_drugs");
        reportData.setReportName("Patient-Dispensed-Drugs");
        reportService.generateReport(reportData, response);
    }
    
}
