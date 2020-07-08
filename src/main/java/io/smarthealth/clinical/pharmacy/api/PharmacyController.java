/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.api;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/pharmacy")
@Api(value = "Pharmacy Controller", description = "Operations pertaining to Pharmacy maintenance")
@RequiredArgsConstructor
public class PharmacyController {

    final PharmacyService pharmService;

    final ModelMapper modelMapper;

    final VisitService visitService;

    final PrescriptionService prescriptionService;

    final ItemService itemService;

    final EmployeeService employeeService;

    final PatientQueueService patientQueueService;

    final DepartmentService departmentService;

    final FacilityService facilityService;

    final ServicePointService servicePointService;
    private final SequenceNumberService sequenceNumberService;
    private final UserService userService;

    @PostMapping("/patient-drug")
    @PreAuthorize("hasAuthority('create_pharmacy')")
    public @ResponseBody
    ResponseEntity<?> savePatientDrugs(@RequestBody @Valid final List<PatientDrugsData> patientdrugsData) {
        //validate visit
        //Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientDrugsData> patientDrugList = pharmService.savePatientDrugs(patientdrugsData);
        return new ResponseEntity<>(patientDrugList, HttpStatus.CREATED);
    }

    @PostMapping("/visit/{visitNo}/prescription")
    @PreAuthorize("hasAuthority('create_pharmacy')")
    public @ResponseBody
    ResponseEntity<?> savePatientPrescriptions(@PathVariable("visitNo") final String visitNumber, @RequestBody @Valid final List<PrescriptionData> prescriptionData) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
//        Employee employee = employeeService.fetchEmployeeByAccountUsername(SecurityUtils.getCurrentUserLogin().get());
//         Employee employee = employeeService.fetchEmployeeByAccountUsername(SecurityUtils.getCurrentUserLogin().get());
        Optional<User> user = userService.findUserByUsernameOrEmail(SecurityUtils.getCurrentUserLogin().get());

        List<Prescription> prescriptions = new ArrayList<>();
        String prescriptionNo = sequenceNumberService.next(1L, Sequences.Prescription.name());

        for (PrescriptionData pd : prescriptionData) {
            Prescription p = PrescriptionData.map(pd);
            p.setPatient(visit.getPatient());
            Item item = itemService.findItemWithNoFoundDetection(pd.getItemCode());
            p.setItem(item);
            p.setVisit(visit);
            p.setOrderDate(LocalDate.now());
            p.setOrderNumber(prescriptionNo);
            p.setRequestedBy(user.get());
            p.setRequestType(RequestType.Pharmacy);
            prescriptions.add(p);
        }

        List<Prescription> saved = prescriptionService.createPrescription(prescriptions);

        //Send patient to queue
        PatientQueue patientQueue = new PatientQueue();

        //Department department = departmentService.findByServicePointTypeAndfacility("Pharmacy", facility);
        ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Pharmacy);

        patientQueue.setServicePoint(servicePoint);
        patientQueue.setPatient(visit.getPatient());
        patientQueue.setSpecialNotes("");
        patientQueue.setUrgency(PatientQueue.QueueUrgency.Medium);
        patientQueue.setStatus(true);
        patientQueue.setVisit(visit);
        PatientQueue savedQueue = patientQueueService.createPatientQueue(patientQueue);
        System.out.println("Submitted to queue");
        List<PrescriptionData> savedlist = new ArrayList<>();
        saved.forEach((p) -> {
            savedlist.add(PrescriptionData.map(p));
        });
        return new ResponseEntity<>(savedlist, HttpStatus.CREATED);
    }

    @GetMapping("/visit/{visitNo}/prescription")
    @PreAuthorize("hasAuthority('view_pharmacy')")
    public @ResponseBody
    ResponseEntity<?> fetchPatientPrescriptionsByVisit(@PathVariable("visitNo") final String visitNumber, Pageable pageable) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        Page<Prescription> prescriptionsPage = prescriptionService.fetchAllPrescriptionsByVisit(visit, pageable);

        Pager<List<PrescriptionData>> pagers = new Pager();

        List<PrescriptionData> pdList = new ArrayList<>();
        for (Prescription p : prescriptionsPage) {
            PrescriptionData data1 = PrescriptionData.map(p);
            pdList.add(data1);
        }
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(pdList);
        PageDetails details = new PageDetails();
        details.setPage(prescriptionsPage.getNumber() + 1);
        details.setPerPage(prescriptionsPage.getSize());
        details.setTotalElements(prescriptionsPage.getTotalElements());
        details.setTotalPage(prescriptionsPage.getTotalPages());
        details.setReportName("Patient prescriptions");
        pagers.setPageDetails(details);

        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    @GetMapping("/patientDrug/{id}")
    @PreAuthorize("hasAuthority('view_pharmacy')")
    public ResponseEntity<?> fetchPatientDrugsById(@PathVariable("id") final Long id) {
        PatientDrugsData patientdrugsdata = pharmService.getById(id);
        if (patientdrugsdata != null) {
            return ResponseEntity.ok(patientdrugsdata);
        } else {
            throw APIException.notFound("container Number {0} not found.", id);
        }
    }

    @GetMapping("/patientDrug")
    @PreAuthorize("hasAuthority('view_pharmacy')")
    public ResponseEntity<?> fetchAllPatientDrugs(
            @RequestParam(value = "visitNumber", defaultValue = "") String visitNumber,
            @RequestParam(value = "patientNumber", defaultValue = "") String patientNumber,
            Pageable pageable) {

        List<PatientDrugsData> patientDrugs = pharmService.getByVisitIdAndPatientId(visitNumber, patientNumber);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/patientDrug/")
                .buildAndExpand().toUri();
        return ResponseEntity.created(location).body(ApiResponse.successMessage("PatientDrugsData returned successfuly", HttpStatus.OK, patientDrugs));
    }

    @DeleteMapping("/patientDrug/{id}")
    @PreAuthorize("hasAuthority('view_pharmacy')")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        pharmService.deletePatientDrug(id);
        return ResponseEntity.ok("200");
    }

}
