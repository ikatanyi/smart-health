/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

//import io.smarthealth.auth.domain.User;
//import io.smarthealth.auth.service.UserService;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.laboratory.service.LaboratoryService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.service.RadiologyService;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.DocResults;
import io.smarthealth.clinical.record.data.HistoricalDiagnosisData;
import io.smarthealth.clinical.record.data.PatientNotesData;
import io.smarthealth.clinical.record.data.ResultsData;
import io.smarthealth.clinical.record.domain.Disease;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.PatientNotes;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DiseaseService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.swagger.annotations.Api;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Simon.Waweru
 */
@RestController
@RequestMapping("/api/consultation")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to general practitioner consultation")
@RequiredArgsConstructor
public class ConsultationController {

    private final PatientNotesService patientNotesService;
    private final PatientService patientService;
    private final VisitService visitService;
    private final UserService userService;
    private final DiseaseService diseaseService;
    private final DiagnosisService diagnosisService;

    private final LaboratoryService laboratoryService;
    private final RadiologyService radiologyService;
    private final DoctorInvoiceService doctorInvoiceService;
    private final EmployeeService employeeService;


    /* Patient Notes */
    @PostMapping("/patient-notes")
    @PreAuthorize("hasAuthority('create_consultation')")
    public @ResponseBody
    ResponseEntity<?> savePatientNotes(Authentication authentication, @Valid @RequestBody PatientNotesData patientNotesData) {
        patientNotesService.ValidateConsultationFields(patientNotesData);
        Visit visit = visitService.findVisitEntityOrThrow(patientNotesData.getVisitNumber());
        User user = userService.findUserByUsernameOrEmail(authentication.getName())
                .orElseThrow(() -> APIException.notFound("Employee login account provided is not valid"));
        Patient patient = patientService.findPatientOrThrow(patientNotesData.getPatientNumber());
        PatientNotes patientNotes = patientNotesService.convertDataToEntity(patientNotesData);

        patientNotes.setVisit(visit);
        patientNotes.setHealthProvider(user);
        patientNotes.setPatient(patient);
        patientNotes.setDateRecorded(LocalDateTime.now());

        //check if notes already exists by visit
        Optional<PatientNotes> patientNoteExisting = patientNotesService.fetchPatientNotesByVisit(visit);
        PatientNotes pns = new PatientNotes();
        if (patientNoteExisting.isPresent()) {
            PatientNotes npn = patientNoteExisting.get();

            //update note
            npn.setChiefComplaint(patientNotesData.getChiefComplaint());
            npn.setExaminationNotes(patientNotesData.getExaminationNotes());
            npn.setHistoryNotes(patientNotesData.getHistoryNotes());
            npn.setSocialHistory(patientNotesData.getSocialHistory());
            npn.setHealthProvider(user);
            pns = patientNotesService.createPatientNote(npn);

        } else {
            pns = patientNotesService.createPatientNote(patientNotes);
            Optional<Employee> healthProvider = employeeService.fetchEmployeeByUserWithoutFoundDetection(user);
            //create doctor invoice if doctor was not specified during visit activation
            if (visit.getHealthProvider() == null) {
                if (healthProvider.isPresent()) {
                    Optional<DoctorItem> chargeableDoctorItem = doctorInvoiceService.getDoctorItem(healthProvider.get(), visit.getClinic().getServiceType());
                    doctorInvoiceService.createDoctorInvoice(visit, healthProvider.get(), chargeableDoctorItem.get());
                }
            }
        }
        PatientNotesData savedData = patientNotesService.convertEntityToData(pns);
        //update consultation queue
        visit.setIsActiveOnConsultation(Boolean.FALSE);
        visitService.createAVisit(visit);
        return ResponseEntity.ok().body(ApiResponse.successMessage("History and examination notes saved successfully", HttpStatus.CREATED, savedData));
    }

    @GetMapping("/visit/{visitNumber}/patient-notes")
    @PreAuthorize("hasAuthority('view_consultation')")
    public @ResponseBody
    ResponseEntity<?> fetchPatientNotesByVisit(@PathVariable("visitNumber") final String visitNumber) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Optional<PatientNotes> pn = patientNotesService.fetchPatientNotesByVisit(visit);
        if (pn.isPresent()) {
            PatientNotesData savedData = patientNotesService.convertEntityToData(pn.get());
            return ResponseEntity.ok().body(ApiResponse.successMessage("Success", HttpStatus.OK, savedData));
        } else {
            return ResponseEntity.ok().body(ApiResponse.successMessage("No records found", HttpStatus.OK, new ArrayList<>()));
        }
    }

    @GetMapping("/patient/{patientNo}/patient-notes")
    @PreAuthorize("hasAuthority('view_consultation')")
    public @ResponseBody
    ResponseEntity<?> fetchPatientNotesByPatient(@PathVariable("patientNo") final String patientNo, final Pageable pageable) {
        Patient patient = patientService.findPatientOrThrow(patientNo);
        Page<PatientNotesData> list = patientNotesService.fetchAllPatientNotesByPatient(patient, pageable).map((n) -> patientNotesService.convertEntityToData(n));
        Pager<List<PatientNotesData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Notes ");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    /* Diagnosis End Points */
    @GetMapping("/disease")
    @PreAuthorize("hasAuthority('view_consultation')")
    public ResponseEntity<?> fetchAllDiseases(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<Disease> list;
        if (search != null) {
            list = diseaseService.filterDiseaseByNameOrCode(search, search, pageable);
        } else {
            list = diseaseService.fetchAllDiseases(pageable);
        }
        Pager<List<Disease>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Diseases");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/visit/{visitNumber}/diagnosis")
    @PreAuthorize("hasAuthority('view_consultation')")
    public ResponseEntity<?> fetchAllDiagnosisByVisit(
            @PathVariable("visitNumber") final String visitNumber,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        DiagnosisData diagnosisData = new DiagnosisData();
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Page<PatientDiagnosis> pdList = diagnosisService.fetchAllDiagnosisByVisit(visit, pageable);

        Page<DiagnosisData> list = pdList.map(pd -> {
            DiagnosisData d = diagnosisData.map(pd);
            d.setPatientData(patientService.convertToPatientData(pd.getPatient()));
            d.setPatientNumber(pd.getPatient().getPatientNumber());
            d.setVisitNumber(visit.getVisitNumber());
            return d;
        });
        Pager<List<DiagnosisData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient diagnosis");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/patients/{patientNo}/diagnosis")
    @PreAuthorize("hasAuthority('view_consultation')")
    public ResponseEntity<?> fetchAllDiagnosisByPatient(
            @PathVariable(value = "patientNo", required = true) final String patientNo,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Patient patient = patientService.findPatientOrThrow(patientNo);
        List<HistoricalDiagnosisData> list = new ArrayList<>();
        Page<Visit> patientVisits = visitService.fetchAllVisits(null, null, null, patientNo, null, false, null, null, null, false, null, false, pageable);
        for (Visit v : patientVisits) {
            HistoricalDiagnosisData dd = new HistoricalDiagnosisData();
            dd.setPatientName(patient.getFullName());
            dd.setPatientNumber(patient.getPatientNumber());
            dd.setStartDate(v.getStartDatetime());
            dd.setStopDatetime(v.getStopDatetime());
            dd.setVisitNotes(v.getComments());
            dd.setVisitNumber(v.getVisitNumber());
            dd.setVisitId(v.getId());

            Page<PatientDiagnosis> pdList = diagnosisService.fetchAllDiagnosisByVisit(v, Pageable.unpaged());

            Page<DiagnosisData> dlist = pdList.map(pd -> {
                return DiagnosisData.map(pd);
            });

            dd.setDiagnosisData(dlist.getContent());
            list.add(dd);
        }

        PagedListHolder visitDiagnosisPage = new PagedListHolder(list);
        visitDiagnosisPage.setPageSize(pageable.getPageSize()); // number of items per page
        visitDiagnosisPage.setPage(pageable.getPageNumber());

        Pager<List<HistoricalDiagnosisData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(visitDiagnosisPage.getPageList());
        PageDetails details = new PageDetails();
        details.setPage(visitDiagnosisPage.getPage() + 1);
        details.setPerPage(visitDiagnosisPage.getPageSize());
        details.setTotalElements(Long.valueOf(visitDiagnosisPage.getPageSize()));
        details.setTotalPage(visitDiagnosisPage.getPageCount());
        details.setReportName("Patient diagnosis");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PostMapping("/diagnosis")
    @PreAuthorize("hasAuthority('create_consultation')")
    public @ResponseBody
    ResponseEntity<?> savePatientDiagnosis(@Valid @RequestBody List<DiagnosisData> diagnosisData) {
        List<PatientDiagnosis> patientDiagnosises = new ArrayList<>();
        DiagnosisData diagnosisDataService = new DiagnosisData();

        for (DiagnosisData data : diagnosisData) {
            PatientDiagnosis patientDiagnosis = new PatientDiagnosis();
            Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
            Patient patient = patientService.findPatientOrThrow(data.getPatientNumber());
            patientDiagnosis = diagnosisDataService.map(data);
            //find diagnosis by code identifier
           Optional<Disease> disease=  diseaseService.findDiseaseByCodeOptional(data.getCode());
            if(disease.isPresent()){
                patientDiagnosis.setMCode(disease.get().getMCode());
            }
            patientDiagnosis.setVisit(visit);
            patientDiagnosis.setPatient(patient);
            patientDiagnosises.add(patientDiagnosis);
        }

        List<PatientDiagnosis> savedDiagnosisList = diagnosisService.createListOfPatientDiagnosis(patientDiagnosises);

        Page<PatientDiagnosis> page = new PageImpl<>(savedDiagnosisList);

        Pager<List<DiagnosisData>> pagers = new Pager();

        List<DiagnosisData> ddList = new ArrayList<>();
        for (PatientDiagnosis data : savedDiagnosisList) {
            DiagnosisData data1 = diagnosisDataService.map(data);
            ddList.add(data1);
        }
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(ddList);
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber() + 1);
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Patient Diagnosis");
        pagers.setPageDetails(details);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pagers);
    }

    @PutMapping("/diagnosis/{id}")
    @PreAuthorize("hasAuthority('create_consultation')")
    public @ResponseBody
    ResponseEntity<?> updatePatientDiagnosis(
            @PathVariable("id") final Long id,
            @Valid @RequestBody DiagnosisData diagnosisData) {
        PatientDiagnosis diagnosis = diagnosisService.updateDiagnosis(id, diagnosisData);

        Pager<DiagnosisData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient Diagnosis");
        pagers.setContent(DiagnosisData.map(diagnosis));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @DeleteMapping("/diagnosis/{id}")
    @PreAuthorize("hasAuthority('create_consultation')")
    public @ResponseBody
    ResponseEntity.BodyBuilder deletePatientDiagnosis(@PathVariable("id") final Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.ok();
    }

    @GetMapping("/consultation-waiting-list")
    @PreAuthorize("hasAuthority('view_consultation')")
    public ResponseEntity<?> consultationWaitingList(
            //@RequestParam(value = "requestParam", required = false) final String requestParam,
            Pageable pageable) {
        Page<Visit> list = visitService.findVisitByStatus(VisitEnum.Status.CheckIn, pageable);
        List<PatientQueueData> patientQueue = new ArrayList<>();
        for (Visit v : list) {
            PatientQueueData q = new PatientQueueData();
            if (v.getServicePoint() != null) {
                q.setServicePointName(v.getServicePoint().getName());
            }
            q.setPatientData(patientService.convertToPatientData(v.getPatient()));
            q.setPatientNumber(v.getPatient().getPatientNumber());
            q.setVisitData(VisitData.map(v));
            q.setVisitNumber(v.getVisitNumber());
            patientQueue.add(q);
        }

        Pager<List<PatientQueueData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(patientQueue);
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Consultation waiting list");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }

    @PutMapping("/results-read/{resultType}")
    public ResponseEntity.BodyBuilder markResultRead(
            @PathVariable("resultType") final String resultType,
            @Valid @RequestBody ResultsData resultsData
    ) {
        if (resultsData.getResultType().equals(DocResults.Type.Laboratory)) {
            LabRegisterTest test = laboratoryService.getLabRegisterTest(resultsData.getResultId());
            laboratoryService.markResultRegisterStatusAsRead(test);
        }

        if (resultsData.getResultType().equals(DocResults.Type.Radiology)) {
            Page<RadiologyResult> radiologyResults = radiologyService.findAllRadiologyResults(resultsData.getVisitNo(), null, null, null, ScanTestState.Completed, null, null, null, Pageable.unpaged());
            for (RadiologyResult result : radiologyResults) {
                radiologyService.markRadiologyResultStatusAsRead(result);
            }
        }

        return ResponseEntity.ok();
    }

    @GetMapping("/results-alert")
    public ResponseEntity<?> getResultsAlert(
            @RequestParam(value = "visitNo", required = false) final String visitNo,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "patientName", required = false) final String patientName,
            @RequestParam(value = "practitionerUsername", required = false) final String practitionerUsername,
            @RequestParam(value = "resultType", required = false) DocResults.Type type,
            @RequestParam(value = "dateRange", required = false) String dateRange,
            @RequestParam(value = "showResultsRead", required = false, defaultValue = "false") Boolean showResultsRead,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        List<DocResults> list = visitService.getPatientResultsAlerts(visitNo, patientNo, type, range, patientName, practitionerUsername, showResultsRead);

        Pager<?> pagers = PaginationUtil.paginateList(list, "Patient Doctor Results alert Queue", "", pageable);
        return ResponseEntity.ok(pagers);
    }
}
