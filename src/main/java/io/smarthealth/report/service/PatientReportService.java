/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.PatientTestsData;
import io.smarthealth.clinical.record.data.SickOffNoteData;
import io.smarthealth.clinical.record.domain.PatientNotes;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
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
import io.smarthealth.report.data.clinical.PatientVisitData;
import java.io.IOException;
import java.sql.SQLException;
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
    private final DoctorRequestService doctorRequestServcie;
    private final ProcedureService procedureService;
    private final LaboratoryService labService;
    private final PatientNotesService patientNotesService;
    private final PharmacyService pharmacyService;
    private final RadiologyService radiologyService;
    private final SickOffNoteService sickOffNoteService;
    
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
    
    public void getPatientCard(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String patientId = reportParam.getFirst("patientId");
        PatientData patientData = patientService.convertToPatientData(patientService.findPatientOrThrow(patientId));
        
        reportData.setData(Arrays.asList(patientData));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientCard");
        reportData.setReportName("Patient-Card");
        reportService.generateReport(reportData, response);
    }
    
    public void getVisit(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws JRException, SQLException, IOException{
        String visitNumber = reportParam.getFirst("visitNumber");
        String staffNumber = reportParam.getFirst("staffNumber");
        String servicePointType = reportParam.getFirst("servicePointType");
        String patientNumber = reportParam.getFirst("patientNumber");
        String patientName = reportParam.getFirst("visitNumber");
        Boolean runningStatus = Boolean.getBoolean(reportParam.getFirst("runningStatus"));
        String dateRange = reportParam.getFirst("dateRange");
        Integer page = Integer.getInteger(reportParam.getFirst("page"));
        Integer size = Integer.getInteger(reportParam.getFirst("size"));
        ReportData reportData = new ReportData();
        
        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        
         List<VisitData> visitData = visitService.fetchAllVisits(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, range, pageable)
                .stream()
                .map((visit) -> visitService.convertVisitEntityToData(visit))
                .collect(Collectors.toList());
        reportData.setData(visitData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientVisit");
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
    
    public void getPatientRequest(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        DoctorRequestData.RequestType requestType = DoctorRequestData.RequestType.valueOf(reportParam.getFirst("requestType"));
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<DoctorRequestData> requestData = doctorRequestServcie.findAllRequestsByVisitAndRequestType(visit, requestType, pageable)
                .getContent()
                .stream()
                .map((test) -> DoctorRequestData.map(test))
                .collect(Collectors.toList());

        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());

        }
        reportData.getFilters().put("SUBREPORT_DIR", "/clinical/");
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/request_form");
        reportData.setReportName(requestType.name() + "_request_form");
        reportService.generateReport(reportData, response);
    }
    
    
     public void getPatientFile(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        
        final String PatientId = reportParam.getFirst("patientId");
        List<PatientVisitData> visitData = new ArrayList();
        PatientVisitData patientVisitData = new PatientVisitData();
        PatientData patient = patientService.convertToPatientData(patientService.findPatientOrThrow(PatientId));
        if (!patient.getAddress().isEmpty()) {
            patientVisitData.setAddressCountry(patient.getAddress().get(0).getCountry());
            patientVisitData.setAddressCounty(patient.getAddress().get(0).getCounty());
            patientVisitData.setAddressLine1(patient.getAddress().get(0).getLine1());
            patientVisitData.setAddressLine2(patient.getAddress().get(0).getLine2());
            patientVisitData.setAddressPostalCode(patient.getAddress().get(0).getPostalCode());
            patientVisitData.setAddressTown(patient.getAddress().get(0).getTown());
        }
        if (!patient.getContact().isEmpty()) {
            patientVisitData.setContactEmail(patient.getContact().get(0).getEmail());
            patientVisitData.setContactMobile(patient.getContact().get(0).getMobile());
            patientVisitData.setContactTelephone(patient.getContact().get(0).getTelephone());
        }
        patientVisitData.setDateOfBirth(String.valueOf(patient.getDateOfBirth()));
        patientVisitData.setFullName(patient.getFullName());
        patientVisitData.setGender(String.valueOf(patient.getGender()));
        patientVisitData.setTitle(patient.getTitle());
        patientVisitData.setPatientId(patient.getPatientNumber());
        Pageable pageable = PaginationUtil.createPage(1, 500);
        List<Visit> visits = visitService.fetchVisitByPatientNumber(PatientId, pageable).getContent();
        if (visits.isEmpty()) {
            visitData.add(patientVisitData);
        }
        for (Visit visit : visits) {
            PatientVisitData pVisitData = patientVisitData;
            List<PatientScanRegisterData> scanData = radiologyService.findPatientScanRegisterByVisit(visit)
                    .stream()
                    .map((scan) -> scan.todata())
                    .collect(Collectors.toList());
            List<PatientProcedureRegisterData> procedures = procedureService.findPatientProcedureRegisterByVisit(visit.getVisitNumber())
                    .stream()
                    .map((proc) -> proc.toData())
                    .collect(Collectors.toList());

             List<LabResultData> labTests = labService.getLabResultDataByVisit(visit);

            Optional<PatientNotes> patientNotes = patientNotesService.fetchPatientNotesByVisit(visit);
            if (patientNotes.isPresent()) {
                PatientNotes notes = patientNotes.get();
                pVisitData.setBriefNotes(notes.getBriefNotes());
                pVisitData.setChiefComplaint(notes.getChiefComplaint());
                pVisitData.setExaminationNotes(notes.getExaminationNotes());
                pVisitData.setHistoryNotes(notes.getHistoryNotes());

            }

            List<DiagnosisData> diagnosisData = diagnosisService.fetchAllDiagnosisByVisit(visit, pageable)
                    .stream()
                    .map((diag) -> DiagnosisData.map(diag))
                    .collect(Collectors.toList());
            List<PatientDrugsData> pharmacyData = pharmacyService.getByVisitIdAndPatientId(visit.getVisitNumber(), PatientId);

            pVisitData.setVisitNumber(visit.getVisitNumber());
            pVisitData.setCreatedOn(String.valueOf(visit.getCreatedOn()));
            pVisitData.setLabTests(labTests);
            pVisitData.setProcedures(procedures);
            pVisitData.setRadiologyTests(scanData);
            pVisitData.setDrugsData(pharmacyData);
            pVisitData.setDiagnosis(diagnosisData);
            pVisitData.setAge(patient.getAge());
            if (visit.getHealthProvider() != null) {
                pVisitData.setPractitionerName(visit.getHealthProvider().getFullName());
            }

            visitData.add(pVisitData);
        }

        List<JRSortField> sortList = new ArrayList();
        ReportData reportData = new ReportData();
        reportData.setPatientNumber(PatientId);
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.ASCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(visitData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/patientFile");
        reportData.setReportName("Patient-file");
        reportService.generateReport(reportData, response);

    }
     
     public void getSickOff(MultiValueMap<String,String>reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<SickOffNoteData> requestData = Arrays.asList(SickOffNoteData.map(sickOffNoteService.fetchSickNoteByVisitWithNotFoundThrow(visit)));
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
        }
        reportData.setTemplate("/patient/sick_off_note");
        reportData.setReportName("sick-off-note");
        reportService.generateReport(reportData, response);
    }
    
}
