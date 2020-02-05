/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

//import io.smarthealth.auth.domain.User;
//import io.smarthealth.auth.service.UserService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.record.data.DiagnosisData;
import io.smarthealth.clinical.record.data.PatientNotesData;
import io.smarthealth.clinical.record.domain.Disease;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.domain.PatientNotes;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.clinical.record.service.DiseaseService;
import io.smarthealth.clinical.record.service.PatientNotesService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Simon.Waweru
 */
@RestController
@RequestMapping("/api/consultation")
@Api(value = "Doctor Request Controller", description = "Operations pertaining to general practitioner consultation")
public class ConsultationController {

    private final PatientNotesService patientNotesService;

    private final PatientService patientService;

    private final VisitService visitService;

    private final EmployeeService employeeService;

//    private final UserService userService;
    private final DiseaseService diseaseService;

    private final DiagnosisService diagnosisService;
    private final DepartmentService departmentService;
    private final SickOffNoteService sickOffNoteService;

    public ConsultationController(PatientNotesService patientNotesService, PatientService patientService, VisitService visitService, EmployeeService employeeService, DiseaseService diseaseService, DiagnosisService diagnosisService, DepartmentService departmentService, SickOffNoteService sickOffNoteService) {
        this.patientNotesService = patientNotesService;
        this.patientService = patientService;
        this.visitService = visitService;
        this.employeeService = employeeService;
        this.diseaseService = diseaseService;
        this.diagnosisService = diagnosisService;
        this.departmentService = departmentService;
        this.sickOffNoteService = sickOffNoteService;
    }

    /* Patient Notes */
    @PostMapping("/patient-notes")
    public @ResponseBody
    ResponseEntity<?> savePatientNotes(Authentication authentication, @Valid @RequestBody PatientNotesData patientNotesData) {
        Visit visit = visitService.findVisitEntityOrThrow(patientNotesData.getVisitNumber());
//        User user = userService.findUserByUsernameOrEmail(authentication.getName())
//                .orElseThrow(() -> APIException.notFound("Employee login account provided is not valid"));
        Patient patient = patientService.findPatientOrThrow(patientNotesData.getPatientNumber());
        PatientNotes patientNotes = patientNotesService.convertDataToEntity(patientNotesData);
        patientNotes.setVisit(visit);
//        patientNotes.setHealthProvider(employeeService.fetchEmployeeByUser(user));
        patientNotes.setPatient(patient);

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
            pns = patientNotesService.createPatientNote(npn);

        } else {
            pns = patientNotesService.createPatientNote(patientNotes);
        }
        PatientNotesData savedData = patientNotesService.convertEntityToData(pns);
        return ResponseEntity.ok().body(ApiResponse.successMessage("History and examination notes saved successfully", HttpStatus.CREATED, savedData));
    }

    @GetMapping("/visit/{visitNumber}/patient-notes")
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

    /* Diagnosis End Points */
    @GetMapping("/disease")
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

    @PostMapping("/diagnosis")
    public @ResponseBody
    ResponseEntity<?> savePatientDiagnosis(@Valid @RequestBody List<DiagnosisData> diagnosisData) {
        List<PatientDiagnosis> patientDiagnosises = new ArrayList<>();
        DiagnosisData diagnosisDataService = new DiagnosisData();

        for (DiagnosisData data : diagnosisData) {
            PatientDiagnosis patientDiagnosis = new PatientDiagnosis();
            Visit visit = visitService.findVisitEntityOrThrow(data.getVisitNumber());
            Patient patient = patientService.findPatientOrThrow(data.getPatientNumber());
            patientDiagnosis = diagnosisDataService.map(data);
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

    @GetMapping("/consultation-waiting-list")
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
}
