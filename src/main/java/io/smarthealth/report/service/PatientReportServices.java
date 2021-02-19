/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.service;

import com.mchange.lang.StringUtils;
import io.smarthealth.administration.codes.domain.Code;
import io.smarthealth.administration.codes.domain.CodeValue;
import io.smarthealth.administration.codes.service.CodeService;
import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecialization;
import io.smarthealth.administration.employeespecialization.service.EmployeeSpecializationService;
import io.smarthealth.appointment.data.AppointmentData;
import io.smarthealth.appointment.service.AppointmentService;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.moh.data.MonthlyMobidity;
import io.smarthealth.clinical.moh.data.Register;
import io.smarthealth.clinical.moh.service.MohService;
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
import io.smarthealth.clinical.record.domain.Referrals;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DoctorRequestService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.record.service.ReferralsService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.TatInterface;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateConverter;
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
import io.smarthealth.report.data.clinical.Moh706LabData;
import io.smarthealth.report.data.clinical.OpData;
import io.smarthealth.report.data.clinical.PatientReportData;
import io.smarthealth.report.data.clinical.PatientVisitData;
import io.smarthealth.report.data.clinical.reportVisitData;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import io.smarthealth.clinical.moh.data.MohData;
import io.smarthealth.security.service.AuditTrailService;
import io.smarthealth.security.data.AuditTrailData;
import java.time.Instant;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class PatientReportServices {

    private final JasperReportsService reportService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final DoctorRequestService doctorRequestServcie;
    private final ProcedureService procedureService;
    private final LaboratoryService labService;
    private final PatientNotesService patientNotesService;
    private final RadiologyService radiologyService;
    private final SickOffNoteService sickOffNoteService;
    private final PrescriptionService prescriptionService;
    private final EmployeeService employeeService;
    private final ReferralsService referralService;
    private final AppointmentService appointmentService;
    private final MohService mohService;
    private final PaymentDetailsService paymentDetailsService;
    private final EmployeeSpecializationService specializationService;
    private final CodeService codeService;
    private final LaboratoryService laboratoryService;
    private final AuditTrailService auditTrailService;

    private final VisitService visitService;

    public void getPatients(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        List<PatientData> patientData = (List<PatientData>) patientService.fetchAllPatients(reportParam.getFirst("term"), reportParam.getFirst("dateRange"), Pageable.unpaged()).getContent()
                .stream()
                .map((patient) -> patientService.convertToPatientData((Patient) patient))
                .collect(Collectors.toList());

        reportData.getFilters().put("range", DateRange.getReportPeriod(DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"))));
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
        String patientName = reportParam.getFirst("patientName");
        Boolean runningStatus = BooleanUtils.toBoolean(reportParam.getFirst("runningStatus"));
        DateRange dateRange = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));

        ReportData reportData = new ReportData();
        reportData.getFilters().put("range", DateRange.getReportPeriod(dateRange));

        List<Visit> visits = visitService.fetchVisitsGroupByVisitNumber(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, dateRange, Pageable.unpaged()).getContent();
        List<reportVisitData> visitArrayList = new ArrayList();
        for (Visit visit : visits) {
            reportVisitData data = new reportVisitData();
            data.setPatientName(visit.getPatient().getFullName());
            data.setPatientNumber(visit.getPatient().getPatientNumber());
            data.setVisitNumber(visit.getVisitNumber());           
            if(visit.getStopDatetime()!=null)
               data.setStopDatetime(DateTimeFormatter.ISO_LOCAL_TIME.format(visit.getStopDatetime()));
            if(visit.getStartDatetime()!=null){
               data.setStartDatetime(DateTimeFormatter.ISO_LOCAL_TIME.format(visit.getStartDatetime()));
               
            }
            data.setDate(visit.getStartDatetime().toLocalDate());
            data.setStartDatetime(String.valueOf(visit.getStartDatetime()));
            List<TatInterface> tatInterFaces = visitService.getPatientTat(visit.getId());
            for (TatInterface interf : tatInterFaces) {
                switch (interf.getServicePoint()) {
                    case "Consultation":
                        data.setConsultation(interf.getTat());
                        break;
                    case "Radiology":
                        data.setRadiology(interf.getTat());
                        break;
                    case "Laboratory":
                        data.setLaboratory(interf.getTat());
                        break;
                    case "Pharmacy":
                        data.setPharmacy(interf.getTat());
                        break;
                    case "Procedure":
                        data.setProcedure(interf.getTat());
                        break;
                    default:
                        data.setOther(interf.getTat());
                        break;
                }
                data.setTotal(data.getTotal() + NumberUtils.createDouble(String.valueOf(interf.getTotal())));
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
        Gender gender = EnumUtils.getEnumIgnoreCase(Gender.class, reportParam.getFirst("gender"));
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
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/diagnosis_report");
        reportData.setReportName("Diagnosis-Report");
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
        Referrals referral = null;
        if (reportParam.getFirst("id") != null) {
            Long id = NumberUtils.createLong(reportParam.getFirst("id"));
            referral = referralService.fetchReferalByIdOrThrowIfNotFound(id);
        } else {
            Visit visit = visitService.findVisitEntityOrThrow(reportParam.getFirst("visitNumber"));
            referral = referralService.fetchLatestReferalByVisit(visit);
        }
        ReferralData referralData = ReferralData.map(referral);

        Visit visit = referral.getVisit();
        reportData.setPatientNumber(referral.getVisit().getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
        }

        PatientVisitData pVisitData = new PatientVisitData();
        pVisitData.setVisitNumber(referral.getVisit().getVisitNumber());

        List<PatientScanTestData> scanData = radiologyService.getPatientScansTestByVisit(visit.getVisitNumber())
                .stream()
                .map((scan) -> scan.toData())
                .collect(Collectors.toList());

        List<PatientProcedureTestData> procedures = procedureService.findProcedureResultsByVisit(visit)
                .stream()
                .map((proc) -> proc.toData())
                .collect(Collectors.toList());

        List<LabResultData> labTests = labService.getResultByVisit(visit)
                .stream()
                .map((test) -> test.toData())
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

        if (visit.getHealthProvider() != null) {
            pVisitData.setPractitionerName(visit.getHealthProvider().getFullName());
        }

        reportData.getFilters().put("drugsData", pharmacyData);
        reportData.getFilters().put("labTests", labTests);
        reportData.getFilters().put("diagnosis", diagnosisData);
        reportData.getFilters().put("scanData", scanData);
        reportData.setData(Arrays.asList(referralData));
        reportData.setFormat(format);
        reportData.setTemplate("/patient/referral");
        reportData.setReportName("Referral_Form");
        reportService.generateReport(reportData, response);
    }

    public void getVisitNote(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Code type = EnumUtils.getEnum(Code.class, reportParam.getFirst("type"));
        String visitNumber = reportParam.getFirst("visitNumber");
        Optional<Visit> visit = visitService.findVisit(visitNumber);
        VisitData visitData = null;
        if (visit.isPresent()) {
            visitData = VisitData.map(visit.get());
        }
        List<CodeValue> codes = codeService.getCodeValues(type);
        String value = null;
        if (codes != null && !codes.isEmpty()) {
            value = codes.get(0).getCodeValue();
        }
        reportData.getFilters().put("value", value);

        reportData.getFilters().put("type", type == Code.CurfewNote ? "Curfew Note" : type == Code.MedicalNote ? "Patient Medical Note" : "Patient Medical Note");
        reportData.setPatientNumber(visitData.getPatientNumber());
        reportData.setEmployeeId(visitData.getPractitionerCode());
        reportData.setData(Arrays.asList(visitData));
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
        if (appointmentData.getPractitionerCode() != null) {
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
        final Boolean showHeader = BooleanUtils.toBoolean(reportParam.getFirst("showHeader"));
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

            List<LabRegisterTestData> labTests = labService.getTestsResultsByVisit(visit.getVisitNumber(), null)
                    .stream()
                    .map((test) -> test.toData(Boolean.TRUE))
                    .collect(Collectors.toList());

            Page<PatientNotes> patientNotes = patientNotesService.fetchAllPatientNotesByVisit(visit, Pageable.unpaged());

            patientNotes.stream().map((notes) -> {
                pVisitData.setBriefNotes(StringUtils.nonNullOrBlank(pVisitData.getBriefNotes()) + "," + StringUtils.nonNullOrBlank(notes.getBriefNotes()));
                return notes;
            }).map((notes) -> {
                pVisitData.setChiefComplaint(StringUtils.nonNullOrBlank(pVisitData.getChiefComplaint()) + "," + StringUtils.nonNullOrBlank(notes.getChiefComplaint()));
                return notes;
            }).map((notes) -> {
                pVisitData.setExaminationNotes(StringUtils.nonNullOrBlank(pVisitData.getExaminationNotes()) + "," + StringUtils.nonNullOrBlank(notes.getExaminationNotes()));
                return notes;
            }).forEachOrdered((notes) -> {
                pVisitData.setHistoryNotes(StringUtils.nonNullOrBlank(pVisitData.getHistoryNotes()) + "," + StringUtils.nonNullOrBlank(notes.getHistoryNotes()));
            });

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
        reportData.getFilters().put("showHeader", showHeader);
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
        SickOffNoteData requestData = SickOffNoteData.map(sickOffNoteService.fetchSickNoteByVisitWithNotFoundThrow(visit));
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        reportData.setData(Arrays.asList(requestData));
        reportData.setFormat(format);
        reportData.setPatientNumber(visit.getPatient().getPatientNumber());
        if (visit.getHealthProvider() != null) {
            reportData.setEmployeeId(visit.getHealthProvider().getStaffNumber());
        }
        reportData.setTemplate("/patient/sick_off_note");
        reportData.setReportName("sick-off-note");
        reportService.generateReport(reportData, response);
    }

    public void getMorbidityReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        Boolean a705=false , b705=false;
        final String name=null;
        System.out.println(reportParam.getFirst("dateRange"));
        DateRange range = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        String term = reportParam.getFirst("term");
        List<MonthlyMobidity> requestDataArray = new ArrayList();
        if(term.equals(">5"))
            a705=true;
        else
            b705=true;
        
        List<MohData> mohList = mohService.getAllMohs(a705, b705, name);
        for(MohData moh:mohList){
            requestDataArray.addAll(mohService.getMonthlyMobidity(range, term, moh.getCode()));
        }
        reportData.setData(requestDataArray);
        reportData.setFormat(format);
        
        LocalDate ld = range.getStartDate();
        Month month = ld.getMonth();
        Integer year = ld.getYear();
        
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.getFilters().put("year", year);
        reportData.getFilters().put("month", month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        reportData.getFilters().put("term", term);
        reportData.setTemplate("/patient/morbidity_monthly");
        reportData.setReportName("morbidity-report");
        reportService.generateReport(reportData, response);
    }

    public void getMohOPAttendanceReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        List<OpData> dataArray = new ArrayList();
        OpData data = null;
        String[] groups = {"Under 1 Year", "1-4 Years", "5-14 Years", "15-24 Years", "25-34 Years", "35-44  Years", "45-49 Years", "50-54 Years", "55-64 Years", "Over 65 Years", "  All Ages  "};
        for (String group : groups) {
            data = new OpData();
            data.setAgeGroup(group);
            data.setNewFemale(0);
            data.setNewMale(0);
            data.setRevFemale(0);
            data.setRevMale(0);
            dataArray.add(data);
        }

        LocalDate date = DateConverter.dateFromString(reportParam.getFirst("date"));
        Date dt1 = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<Visit> visits = visitService.fetchVisitAttendance(dt1);
        for (Visit visit : visits) {
            OpData data2 = dataArray.get(9);
            OpData data3 = dataArray.get(10);
            Integer age = visit.getPatient().getAge();
            Gender gender = visit.getPatient().getGender();
            if (age < 1) {
                data2 = dataArray.get(0);
            }
            if (age >= 1 && age <= 4) {
                data2 = dataArray.get(1);
            }
            if (age >= 5 && age <= 14) {
                data2 = dataArray.get(2);
            }
            if (age >= 15 && age <= 24) {
                data2 = dataArray.get(3);
            }
            if (age >= 25 && age <= 34) {
                data2 = dataArray.get(4);
            }
            if (age >= 35 && age <= 44) {
                data2 = dataArray.get(5);
            }
            if (age >= 45 && age <= 49) {
                data2 = dataArray.get(6);
            }
            if (age >= 50 && age <= 54) {
                data2 = dataArray.get(7);
            }
            if (age >= 55 && age <= 64) {
                data2 = dataArray.get(8);
            }
            if (age >= 65) {
                data2 = dataArray.get(9);
            }

            if (gender == Gender.M) {
                if (visit.getPatient().getDateRegistered() == visit.getStartDatetime().toLocalDate()) {
                    data2.setNewMale(data2.getNewMale() + 1);
                    data3.setNewMale(data3.getNewMale() + 1);
                } else {
                    data2.setRevMale(data2.getRevMale() + 1);
                    data3.setRevMale(data3.getRevMale() + 1);
                }
            } else if (visit.getPatient().getDateRegistered() == visit.getStartDatetime().toLocalDate()) {
                data2.setNewFemale(data2.getNewFemale() + 1);
                data3.setNewFemale(data3.getNewFemale() + 1);
            } else {
                data2.setRevFemale(data2.getRevFemale() + 1);
                data3.setRevFemale(data3.getRevFemale() + 1);
            }
        }
        reportData.setData(dataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/rpt_OP_Statement");
        reportData.setReportName("out-patient-summary");
        reportService.generateReport(reportData, response);
    }

    public void getMoh706LabReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String dateRange = reportParam.getFirst("dateRange");
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Moh706LabData anc = new Moh706LabData();

        List<Moh706LabData> Moh706LabDataArray = labService.getTestsByDate(range)
                .stream()
                .map((register) -> {
                    Moh706LabData values = new Moh706LabData();    
                    
                    Integer age = 0;
                    Integer total = 0;
                    Gender gender = null;
                    List<LabRegisterTest> tests = labService.getLabTestsByDate(register.getLabTest(),range.getStartDateTime(),range.getEndDateTime());
                    for (LabRegisterTest test : tests) {
                        String patientNo = test.getLabRegister().getPatientNo();
                        values.setTestName(test.getLabTest().getTestName());
                        if(test.getLabTest().getIsPanel()){

                        }
                        Optional<PatientData> patient = patientService.fetchPatientByPatientNumber(patientNo);
                        if (patient.isPresent()) {
                            age = patient.get().getAge();
                            gender = patient.get().getGender();
                        }
                        if (age < 5) {
                            values.setUnder5(values.getUnder5()+1);
                        } else {
                            values.setOver5(values.getOver5()+1);
                        }
                        if (gender == Gender.M) {
                            values.setMale(values.getMale()+1);
                        } else {
                            values.setFemale(values.getFemale()+1);
                        } 
                        values.setTotal(values.getTotal()+1);
                    }
                   
                    return values;
                })
                .collect(Collectors.toList());
        Moh706LabDataArray.addAll(getPanelLists(range));
        reportData.setData(Moh706LabDataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/moh706_lab_report");
        reportData.setReportName("moh706_lab_report");
        reportService.generateReport(reportData, response);
    }

    public List<Moh706LabData> getPanelLists(DateRange range){
        return labService.getPanels()
                .stream()
                .map((testPanel) -> {
                    Moh706LabData values = new Moh706LabData();
                    Integer age = 0;
                    Integer total = 0;
                    Gender gender = null;
                    List<LabRegisterTest> tests = labService.getPanelTestsByDate(testPanel.getId(), range.getStartDateTime(),range.getEndDateTime());
                    for (LabRegisterTest test : tests) {
                        String patientNo = test.getLabRegister().getPatientNo();
                        values.setTestName(test.getParentLabTest().getTestName());
                        Optional<PatientData> patient = patientService.fetchPatientByPatientNumber(patientNo);
                        if (patient.isPresent()) {
                            age = patient.get().getAge();
                            gender = patient.get().getGender();
                        }
                        if (age < 5) {
                            values.setUnder5(values.getUnder5()+1);
                        } else {
                            values.setOver5(values.getOver5()+1);
                        }
                        if (gender == Gender.M) {
                            values.setMale(values.getMale()+1);
                        } else {
                            values.setFemale(values.getFemale()+1);
                        }
                        values.setTotal(values.getTotal()+1);
                    }

                    return values;
                })
                .collect(Collectors.toList());

    }

    public void getPatientRegisterReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        DateRange range = DateRange.fromIsoString(reportParam.getFirst("dateRange"));
        List<Register> requestData = visitService.getPatientRegister(range);
        reportData.setData(requestData);
        reportData.setFormat(format);
        reportData.getFilters().put("range", DateRange.getReportPeriod(range));
        reportData.setTemplate("/patient/patient_register");
        reportData.setReportName("Patient-Register");
        reportService.generateReport(reportData, response);
    }

    public void getPatientReport(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        List<PatientReportData> dataArray = new ArrayList();
        PatientReportData data = null;
        String visitNumber = reportParam.getFirst("visitNumber");
        String staffNumber = reportParam.getFirst("staffNumber");
        String servicePointType = reportParam.getFirst("servicePointType");
        String patientNumber = reportParam.getFirst("patientNumber");
        String patientName = reportParam.getFirst("patientName");
        Boolean runningStatus = BooleanUtils.toBoolean(reportParam.getFirst("runningStatus"));
        DateRange dateRange = DateRange.fromIsoStringOrReturnNull(reportParam.getFirst("dateRange"));
        List<Visit> visits = visitService.fetchVisitsGroupByVisitNumber(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, dateRange, Pageable.unpaged()).getContent();

        for (Visit visit : visits) {
            data = new PatientReportData();
            if (visit.getPatient().getDateRegistered() == visit.getStartDatetime().toLocalDate()) {
                data.setStatus("New");
            } else {
                data.setStatus("Revisit");
            }
            data.setDate(visit.getStartDatetime().toLocalDate());

            data.setPatientName(visit.getPatient().getFullName());
            data.setPatientNumber(visit.getPatient().getPatientNumber());
            data.setPaymentMode(visit.getPaymentMethod());
            Optional<PaymentDetails> pdetails = paymentDetailsService.getPaymentDetailsByVist(visit);
            if (pdetails.isPresent()) {
                data.setInsuranceName(pdetails.get().getPayer().getPayerName());
                data.setSchemeName(pdetails.get().getScheme().getSchemeName());
            }

            if (visit.getHealthProvider() != null) {
                Optional<EmployeeSpecialization> spec = specializationService.fetchOptionalSpecializationById(NumberUtils.isNumber(visit.getHealthProvider().getSpecialization()) ? NumberUtils.createLong(visit.getHealthProvider().getSpecialization()) : 1L);
                if (spec.isPresent()) {
                    data.setService(spec.get().getSpecialization());
                }
            }
            data.setServiceType(visit.getServiceType().name());
            data.setVisitNumber(visit.getVisitNumber());
            dataArray.add(data);
        }

        List<JRSortField> sortList = new ArrayList();
        reportData.setPatientNumber(patientNumber);
        JRDesignSortField sortField = new JRDesignSortField();
        sortField.setName("service");
        sortField.setOrder(SortOrderEnum.DESCENDING);
        sortField.setType(SortFieldTypeEnum.FIELD);
        sortList.add(sortField);
        reportData.getFilters().put(JRParameter.SORT_FIELDS, sortList);

        reportData.getFilters().put("range", DateRange.getReportPeriod(dateRange));
        reportData.setData(dataArray);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/PatientAttendancev2Report");
        reportData.setReportName("Patient_Attendance_Report");
        reportService.generateReport(reportData, response);
    }
    
    public void getAuditTrail(MultiValueMap<String, String> reportParam, ExportFormat format, HttpServletResponse response) throws SQLException, JRException, IOException {
        ReportData reportData = new ReportData();
        String dateRange = reportParam.getFirst("dateRange");
        String name = reportParam.getFirst("name");
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        List<AuditTrailData> auditData = auditTrailService.findAll(range, name, Pageable.unpaged())
                .getContent()
                .stream()
                .map(x->x.toData())
                .collect(Collectors.toList());
        
        reportData.setData(auditData);
        reportData.setFormat(format);
        reportData.setTemplate("/patient/audit_report");
        reportData.setReportName("Audit_Report");
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

    private Gender genderToEnum(String gender) {
        if (gender == null || gender.equals("null") || gender.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(Gender.class, gender)) {
            return Gender.valueOf(gender);
        }
        throw APIException.internalError("RequestType a Valid Bill Status");
    }

}
