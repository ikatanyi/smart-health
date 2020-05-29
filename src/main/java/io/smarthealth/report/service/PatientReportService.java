/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.appointment.service.AppointmentService;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.service.ProcedureService;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.PatientTestsData;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.data.ReferralData;
import io.smarthealth.clinical.record.data.SickOffNoteData;
import io.smarthealth.clinical.record.domain.PatientNotes;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.record.service.ReferralsService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.reports.domain.ExportFormat;
import io.smarthealth.infrastructure.reports.service.JasperReportsService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.report.data.ReportData;
import io.smarthealth.report.data.clinical.EmployeeBanner;
import io.smarthealth.report.data.clinical.PatientVisitData;
import io.smarthealth.report.data.clinical.reportVisitData;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    private final PrescriptionService prescriptionService;
    private final EmployeeService employeeService;
    private final ReferralsService referralService;
    private final AppointmentService appointmentService;

    private final VisitService visitService;

    public void getPatients(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
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
        reportData.setReportName("Patient-list");
        reportService.generateReport(reportData, response);
    }

    public void getPatientCard(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String patientId = reportParam.getFirst("patientId");
        PatientData patientData = patientService.convertToPatientData(patientService.findPatientOrThrow(patientId));

        reportData.setData(Arrays.asList(patientData));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientCard");
        reportData.setReportName("Patient-Card");
        reportService.generateReport(reportData, response);
    }

    public void getVisit(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws JRException, SQLException, IOException {
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
        reportData.getFilters().put("range", range);
        List<Visit> patientvisits = visitService.fetchVisitsGroupByVisitNumber(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, range, Pageable.unpaged()).getContent();

        List<reportVisitData>visitArrayList = new ArrayList();
        for (Visit visit : patientvisits) {
            List<Visit> visits1 = visitService.fetchVisitByPatientNumberAndVisitNumber(visit.getPatient().getPatientNumber(), visit.getVisitNumber(), Pageable.unpaged()).getContent();
            
            reportVisitData data = reportVisitData.map(visits1.get(0));
            data.setDuration(Math.abs(Duration.between(data.getStopDatetime(), data.getStartDatetime()).toMinutes()));
            for (Visit visit2 : visits1) {
                switch (visit2.getServicePoint().getServicePointType()) {
                    case Consultation:
                        data.setConsultation(Boolean.TRUE) ;
                        break;
                    case Triage:
                        data.setTriage(Boolean.TRUE);
                        break;
                    case Radiology:
                        data.setRadiology(Boolean.TRUE);
                        break;
                    case Laboratory:
                        data.setLaboratory( Boolean.TRUE);
                        break;
                    case Pharmacy:
                        data.setPharmacy(Boolean.TRUE);
                        break;
                    case Procedure:
                        data.setProcedure(Boolean.TRUE);
                        break;
                    default:
                        data.setOther(Boolean.TRUE);
                        break;
                }
            }
            visitArrayList.add(data);
        }

        List<JRSortField> sortList = new ArrayList();
        reportData.setPatientNumber(patientNumber);
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);

        reportData.setData(visitArrayList);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientVisit");
        reportData.setReportName("visit-report");
        reportService.generateReport(reportData, response);
    }

    public void getDiagnosis(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws JRException, SQLException, IOException {
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
        reportData.setTemplate("/clinical/diagnosis_report");
        reportData.setReportName("visit-report");
        reportService.generateReport(reportData, response);
    }

    public void getPatientRequest(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        DoctorRequestData.RequestType requestType = requestTypeToEnum(reportParam.getFirst("requestType"));
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<DoctorRequestData> requestData = doctorRequestServcie.findAllRequestsByVisitAndRequestType(visit, requestType, Pageable.unpaged())
                .getContent()
                .stream()
                .map((test) -> DoctorRequestData.map(test))
                .collect(Collectors.toList());

        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());

        }
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/request_form");
        reportData.setReportName(requestType + "_request_form");
        reportService.generateReport(reportData, response);
    }
    
    public void getReferralForm(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        ReferralData referralData = ReferralData.map(referralService.fetchReferalByVisitOrThrowIfNotFound(visit));
                

        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());

        }
        reportData.setData(Arrays.asList(referralData));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/referral_form");
        reportData.setReportName("Referral_Form");
        reportService.generateReport(reportData, response);
    }
    
    public void getVisitNote(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        VisitData visit = VisitData.map(visitService.findVisitEntityOrThrow(visitNumber));        
                

        reportData.setPatientNumber(visit.getPatientNumber());
        if (visit.getPractitionerCode() != null) {
            reportData.setEmployeeId(visit.getPractitionerCode());

        }
        reportData.setData(Arrays.asList(visit));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/visit_note");
        reportData.setReportName("Medical-Note");
        reportService.generateReport(reportData, response);
    }
    
    public void getAppointmentLetter(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String appointmentNumber = reportParam.getFirst("appointmentNumber");
        AppointmentData appointmentData = AppointmentData.map(appointmentService.fetchAppointmentByNo(appointmentNumber));
        
         reportData.setPatientNumber(appointmentData.getPatientNumber());
        if (appointmentData.getPractitionerCode()!= null) {
            reportData.setEmployeeId(appointmentData.getPractitionerCode());
        }
            
        reportData.setData(Arrays.asList(appointmentData));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/appointment_letter");
        reportData.setReportName("Appointment-Letter");
        reportService.generateReport(reportData, response);
    }

    public void getPatientFile(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {

        final String patientNumber = reportParam.getFirst("patientNumber");
        final String visitNumber = reportParam.getFirst("visitNumber");
        List<PatientVisitData> visitData = new ArrayList();
        PatientData patient = patientService.convertToPatientData(patientService.findPatientOrThrow(patientNumber));

        List<Visit> visits = null;

        if (visitNumber != null) {
            visits = visitService.fetchVisitByPatientNumberAndVisitNumber(patientNumber, visitNumber, Pageable.unpaged()).getContent();
        } else {
            visits = visitService.fetchVisitByPatientNumber(patientNumber, Pageable.unpaged()).getContent();
        }
        if (visits.isEmpty()) {
            visitData.add(new PatientVisitData());
        }
        for (Visit visit : visits) {
            PatientVisitData pVisitData = new PatientVisitData();
            pVisitData.setVisitNumber(visit.getVisitNumber());

            if (visit.getHealthProvider() != null) {
                Optional<Employee> employee = employeeService.findEmployeeByStaffNumber(visit.getHealthProvider().getStaffNumber());
                if (employee.isPresent()) {
                    pVisitData.getEmployeeData().add(EmployeeBanner.map(employeeService.convertEmployeeEntityToEmployeeData(employee.get())));
                }
            }

            List<PatientScanTestData> scanData = radiologyService.getPatientScansTestByVisit(visit.getVisitNumber())
                    .stream()
                    .map((scan) -> scan.toData())
                    .collect(Collectors.toList());

            List<PatientProcedureTestData> procedures = procedureService.findProcedureResultsByVisit(visit)
                    .stream()
                    .map((proc) -> proc.toData())
                    .collect(Collectors.toList());

            List<LabRegisterTestData> labTests = labService.getTestsResultsByVisit(visit.getVisitNumber(), "")
                    .stream()
                    .map((test) -> test.toData(Boolean.TRUE))
                    .collect(Collectors.toList());

            Optional<PatientNotes> patientNotes = patientNotesService.fetchPatientNotesByVisit(visit);
            if (patientNotes.isPresent()) {
                PatientNotes notes = patientNotes.get();
                pVisitData.setBriefNotes(notes.getBriefNotes());
                pVisitData.setChiefComplaint(notes.getChiefComplaint());
                pVisitData.setExaminationNotes(notes.getExaminationNotes());
                pVisitData.setHistoryNotes(notes.getHistoryNotes());

            }

            List<DiagnosisData> diagnosisData = diagnosisService.fetchAllDiagnosisByVisit(visit, Pageable.unpaged())
                    .stream()
                    .map((diag) -> DiagnosisData.map(diag))
                    .collect(Collectors.toList());

            List<PrescriptionData> pharmacyData = prescriptionService.fetchAllPrescriptionsByVisit(visit, Pageable.unpaged()).getContent()
                    .stream()
                    .map((presc) -> PrescriptionData.map(presc))
                    .collect(Collectors.toList());

            pVisitData.setVisitNumber(visit.getVisitNumber());
            pVisitData.setCreatedOn(String.valueOf(visit.getCreatedOn()));
            pVisitData.getLabTests().addAll(labTests);
            pVisitData.getProcedures().addAll(procedures);
            pVisitData.getRadiologyTests().addAll(scanData);
            pVisitData.getDrugsData().addAll(pharmacyData);
            pVisitData.getDiagnosis().addAll(diagnosisData);
            pVisitData.setAge(patient.getAge());
            if (visit.getHealthProvider() != null) {
                pVisitData.setPractitionerName(visit.getHealthProvider().getFullName());
            }

            visitData.add(pVisitData);
        }

        List<JRSortField> sortList = new ArrayList();
        ReportData reportData = new ReportData();
        reportData.setPatientNumber(patientNumber);
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("visitNumber");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);
        reportData.setData(visitData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/patientFile");
        reportData.setReportName("Patient-file");
        reportService.generateReport(reportData, response);

    }

    public void getSickOff(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String visitNumber = reportParam.getFirst("visitNumber");
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        
        if (visit.getHealthProvider() != null) {
                reportData.getFilters().put("practionerName", visit.getHealthProvider().getFullName());
            }
        
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

    private RequestType requestTypeToEnum(String requestType) {
        if (requestType == null || requestType.equals("null") || requestType.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(RequestType.class, requestType)) {
            return RequestType.valueOf(requestType);
        }
        throw APIException.internalError("RequestType a Valid Bill Status");
    }
}
