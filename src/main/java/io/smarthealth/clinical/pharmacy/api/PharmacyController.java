/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.api;

import io.smarthealth.clinical.pharmacy.data.PatientDrugsData;
import io.smarthealth.clinical.pharmacy.service.PharmacyService;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.PrescriptionData;
import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.service.PrescriptionService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.SecurityUtils;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 *
 * @author Kennedy.Imbenzi
 */
@RestController
@RequestMapping("/api/pharmacy")
@Api(value = "Pharmacy Controller", description = "Operations pertaining to Pharmacy maintenance")
public class PharmacyController {

    final PharmacyService pharmService;

    final ModelMapper modelMapper;

    final VisitService visitService;

    final PrescriptionService prescriptionService;

    final ItemService itemService;

    final SequenceService sequenceService;

    final EmployeeService employeeService;

    final PatientQueueService patientQueueService;

    final DepartmentService departmentService;

    final FacilityService facilityService;

    public PharmacyController(PharmacyService pharmService, ModelMapper modelMapper, VisitService visitService, PrescriptionService prescriptionService, ItemService itemService, SequenceService sequenceService, EmployeeService employeeService, PatientQueueService patientQueueService, DepartmentService departmentService, FacilityService facilityService) {
        this.pharmService = pharmService;
        this.modelMapper = modelMapper;
        this.visitService = visitService;
        this.prescriptionService = prescriptionService;
        this.itemService = itemService;
        this.sequenceService = sequenceService;
        this.employeeService = employeeService;
        this.patientQueueService = patientQueueService;
        this.departmentService = departmentService;
        this.facilityService = facilityService;
    }

    @PostMapping("/patient-drug")
    public @ResponseBody
    ResponseEntity<?> savePatientDrugs(@RequestBody @Valid final List<PatientDrugsData> patientdrugsData) {
        //validate visit
        //Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        List<PatientDrugsData> patientDrugList = pharmService.savePatientDrugs(patientdrugsData);
        return new ResponseEntity<>(patientDrugList, HttpStatus.CREATED);
    }

    @PostMapping("/visit/{visitNo}/prescription")
    public @ResponseBody
    ResponseEntity<?> savePatientPrescriptions(@PathVariable("visitNo") final String visitNumber, @RequestBody @Valid final List<PrescriptionData> prescriptionData) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Employee employee = employeeService.fetchEmployeeByAccountUsername(SecurityUtils.getCurrentUserLogin().get());
        List<Prescription> prescriptions = new ArrayList<>();
        String prescriptionNo = sequenceService.nextNumber(SequenceType.PrescriptionNo);

        for (PrescriptionData pd : prescriptionData) {
            Prescription p = PrescriptionData.map(pd);
            p.setPatient(visit.getPatient());
            Item item = itemService.findItemWithNoFoundDetection(pd.getItemCode());
            p.setItem(item);
            p.setItemCostRate(item.getCostRate());
            p.setItemRate(item.getRate());
            p.setVisit(visit);
            p.setOrderNumber(prescriptionNo);
            p.setRequestedBy(employee);
            p.setRequestType("Pharmacy");
            prescriptions.add(p);
        }

        List<Prescription> saved = prescriptionService.createPrescription(prescriptions);

        //Send patient to queue
        PatientQueue patientQueue = new PatientQueue();
        //logged in facility 
        Facility facility = facilityService.loggedFacility();
        Department department = departmentService.findByServicePointTypeAndfacility("Pharmacy", facility);
        patientQueue.setDepartment(department);
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
    public ResponseEntity<?> fetchPatientDrugsById(@PathVariable("id") final Long id) {
        PatientDrugsData patientdrugsdata = pharmService.getById(id);
        if (patientdrugsdata != null) {
            return ResponseEntity.ok(patientdrugsdata);
        } else {
            throw APIException.notFound("container Number {0} not found.", id);
        }
    }

    @GetMapping("/patientDrug")
    public ResponseEntity<?> fetchAllPatientDrugs(
            @RequestParam(value = "visitNumber", defaultValue = "") String visitNumber,
            @RequestParam(value = "patientNumber", defaultValue = "") String patientNumber,
            Pageable pageable) {

        List<PatientDrugsData> patientDrugs = pharmService.getByVisitIdAndPatientId(visitNumber, patientNumber);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/api/lab/patientDrug/")
                .buildAndExpand().toUri();
        return ResponseEntity.created(location).body(APIResponse.successMessage("PatientDrugsData returned successfuly", HttpStatus.OK, patientDrugs));
    }

    @DeleteMapping("/patientDrug/{id}")
    public ResponseEntity<?> deleteSpecimen(@PathVariable("id") final Long id) {
        pharmService.deletePatientDrug(id);
        return ResponseEntity.ok("200");
    }

}
