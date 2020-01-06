/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.api;

import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.VitalRecordData;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.record.service.TriageService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.APIResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
@Api(value = "Patient Visit", description = "Operations pertaining to patient visit in a health facility")
public class ClinicalVisitController {

    @Autowired
    private VisitService visitService;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    PatientService patientService;
    @Autowired
    TriageService triageService;

    @Autowired
    PatientQueueService patientQueueService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    SequenceService sequenceService;

    @PostMapping("/visits")
    @ApiOperation(value = "Submit a new patient visit", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> addVisitRecord(@RequestBody @Valid final VisitData visitData) {
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        Department department = departmentService.fetchDepartmentByCode(visitData.getDepartmentCode());
        Visit visit = VisitData.map(visitData);
        //generate visit number
        visit.setVisitNumber(sequenceService.nextNumber(SequenceType.VisitNumber)/*String.valueOf(visitService.generateVisitNumber())*/);
        visit.setStartDatetime(visitData.getStartDatetime());
        visit.setPatient(patient);
        visit = this.visitService.createAVisit(visit);
        //Push it to queue

        PatientQueue patientQueue = new PatientQueue();
        patientQueue.setDepartment(department);
        patientQueue.setPatient(patient);
        patientQueue.setStatus(true);
        patientQueue.setVisit(visit);
        patientQueueService.createPatientQueue(patientQueue);
        //Convert to data
        VisitData visitDat = modelMapper.map(visit, VisitData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}")
                .buildAndExpand(visit.getVisitNumber()).toUri();

        return ResponseEntity.created(location).body(APIResponse.successMessage("Visit was activated successfully", HttpStatus.CREATED, visitDat));
    }

    @PutMapping("/visits/{visitNumber}")
    @ApiOperation(value = "Update patient visit record", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitRecord(@PathVariable("visitNumber") final String visitNumber, @RequestBody @Valid final VisitData visitData) {
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        visit.setScheduled(visitData.getScheduled());
        visit.setStartDatetime(visitData.getStartDatetime());
        visit.setStopDatetime(visitData.getStopDatetime());
        visit.setVisitNumber(visitData.getVisitNumber());
        visit.setVisitType(visitData.getVisitType());
        visit.setStatus(visitData.getStatus());
        visit.setPatient(patient);
        visit = this.visitService.createAVisit(visit);
        //Convert to data
        VisitData visitDat = modelMapper.map(visit, VisitData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}")
                .buildAndExpand(visit.getVisitNumber()).toUri();

        return ResponseEntity.created(location).body(visitDat);
    }

    @GetMapping("/visits")
    public ResponseEntity<List<VisitData>> fetchAllVisits(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<VisitData> page = visitService.fetchAllVisits(pageable).map(v -> convertToVisitData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{id}/visits")
    public ResponseEntity<List<VisitData>> fetchAllVisitsByPatient(@PathVariable("id") final String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        System.out.println("patientNumber " + patientNumber);
        Page<VisitData> page = visitService.fetchVisitByPatientNumber(patientNumber, pageable).map(v -> convertToVisitData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/visits/{visitNumber}/vitals")
    @ApiOperation(value = "Create/Add a new patient vital by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByVisit(@PathVariable("visitNumber") String visitNumber, @RequestBody @Valid final VitalRecordData vital) {
        VitalsRecord vitalR = this.triageService.addVitalRecordsByVisit(visitNumber, vital);

        VitalRecordData vr = modelMapper.map(vitalR, VitalRecordData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}/vitals/{id}")
                .buildAndExpand(visitNumber, vitalR.getId()).toUri();

        return ResponseEntity.created(location).body(vr);
    }

    @PostMapping("/patient/{patientNo}/vitals")
    @ApiOperation(value = "", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByPatient(@PathVariable("patientNo") String patientNo, @RequestBody @Valid final VitalRecordData vital) {
        VitalsRecord vitalR = this.triageService.addVitalRecordsByPatient(patientNo, vital);
        System.out.println("vital.getUrgency() " + vital.getUrgency());
        //Update queue
        PatientQueue patientQueue = patientQueueService.fetchQueueByVisitNumber(vitalR.getVisit());
        patientQueue.setUrgency(PatientQueue.QueueUrgency.valueOf(vital.getUrgency()));
        if (vital.getSendTo().equals("specialist")) {
            Employee employee = employeeService.fetchEmployeeByNumberOrThrow(vital.getStaffNumber());
            patientQueue.setStaffNumber(employee);
            patientQueue.setDepartment(employee.getDepartment());
            patientQueue.setSpecialNotes("Sent from triage");
        } else if (vital.getSendTo().equals("department")) {
            Department department = departmentService.fetchDepartmentByCode(vital.getDepartmentCode());
            patientQueue.setDepartment(department);
            patientQueue.setSpecialNotes("Sent from triage");
        } else {
            //patientQueue.setStatus(false);
        }
        patientQueueService.createPatientQueue(patientQueue);
        VitalRecordData vr = modelMapper.map(vitalR, VitalRecordData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/patient/{patientNo}/vitals/{id}")
                .buildAndExpand(patientNo, vitalR.getId()).toUri();

        return ResponseEntity.created(location).body(vr);
    }

    @GetMapping("/visits/{visitNumber}/vitals")
    @ApiOperation(value = "Fetch all patient vitals by visits", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByVisit(@PathVariable("visitNumber") final String visitNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<VitalRecordData> page = triageService.fetchVitalRecordsByVisit(visitNumber, pageable).map(v -> convertToVitalsData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{patientNumber}/vitals")
    @ApiOperation(value = "Fetch all patient vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByPatient(@PathVariable("patientNumber") final String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<VitalRecordData> page = triageService.fetchVitalRecordsByPatient(patientNumber, pageable).map(v -> convertToVitalsData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private VisitData convertToVisitData(Visit visit) {
        VisitData visitData = modelMapper.map(visit, VisitData.class);
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        visitData.setPatientData(patientService.convertToPatientData(patient));
        return visitData;
    }

    private VitalRecordData convertToVitalsData(VitalsRecord vitalsRecord) {
        return modelMapper.map(vitalsRecord, VitalRecordData.class);
    }

}
