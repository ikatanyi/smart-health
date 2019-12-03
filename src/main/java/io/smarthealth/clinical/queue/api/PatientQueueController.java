/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.api;

import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.Facility;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "Patient Queue", description = "End points pertaining to patient departmental queue")
public class PatientQueueController {

    @Autowired
    PatientService patientService;

    @Autowired
    PatientQueueService patientQueueService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/department/{servicePoint}/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueuesByDepartment(@PathVariable("servicePoint") final String servicePoint, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        boolean status = true;
        if (queryParams.getFirst("status") != null) {
            status = Boolean.valueOf(queryParams.getFirst("status"));
        }
        System.out.println("status " + status);
        //Facility facility = facilityService.findFacility(Long.valueOf("1"));
        Department department = departmentService.findByServicePointTypeAndfacility(servicePoint, facilityService.loggedFacility());
        Page<PatientQueueData> page = patientQueueService.fetchQueueByDept(department, status, pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
//    @GetMapping("/facility/{fId}/department/{deptId}/queue")
//    public ResponseEntity<List<PatientQueueData>> fetchQueuesByDepartment(@PathVariable("fId") final Long fId, @PathVariable("deptId") final Long deptId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
//        Facility facility = facilityService.findFacility(fId);
//        Department department = departmentService.fetchDepartmentById(deptId);
//        Page<PatientQueueData> page = patientQueueService.fetchQueueByDept(department, pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

    @GetMapping("/patient/{patientNo}/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueuesBPatient(@PathVariable("patientNo") String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Patient patient = patientService.findPatientOrThrow(patientNumber);
        Page<PatientQueueData> page = patientQueueService.fetchQueueByPatient(patient, pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueue(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<PatientQueueData> page = patientQueueService.fetchQueue(pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/queue/{queueNo}/deactivate-queue")
    public ResponseEntity<?> deactivateFromQueue(@PathVariable("queueNo") final Long queueNo) {
        PatientQueue pq = patientQueueService.fetchQueueByID(queueNo);
        pq.setStatus(false);
        patientQueueService.createPatientQueue(pq);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
