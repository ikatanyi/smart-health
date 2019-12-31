/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.api;

import io.smarthealth.administration.servicepoint.data.ServicePointData;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.service.DepartmentService;
import io.smarthealth.organization.facility.service.FacilityService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private VisitService visitService;

    @Autowired
    private ServicePointService servicePointService;

    @GetMapping("/department/{servicePoint}/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueuesByDepartment(@PathVariable("servicePoint") final String servicePoint, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        boolean status = true;
        if (queryParams.getFirst("status") != null) {
            status = Boolean.valueOf(queryParams.getFirst("status"));
            /*
             private String patientNumber;
    private String visitNumber;
    private String departmentId;

    private ServicePointData servicePointData;
    private VisitData visitData;
    private PatientData patientData;
    private Long id;
    private String urgency;
    private String specialNotes;
    private  String servicePointName;
    
             */
        }
        List<PatientQueueData> patientQueue = new ArrayList<>();

        ServicePoint serviceP = servicePointService.getServicePointByType(ServicePointType.valueOf(servicePoint));

        Page<Visit> page = visitService.findVisitByServicePoint(serviceP, pageable);
        //.map(q -> patientQueueService.convertToPatientQueueData(q));

        for (Visit visit : page.getContent()) {
            PatientQueueData pq = new PatientQueueData();
            pq.setPatientData(patientService.convertToPatientData(visit.getPatient()));
            pq.setPatientNumber(visit.getPatient().getPatientNumber());
            pq.setServicePointData(ServicePointData.map(serviceP));
            pq.setServicePointName(serviceP.getName());
            //pq.setSpecialNotes(visit.);
            //pq.setUrgency(visit.get);
            pq.setVisitData(visitService.convertVisitEntityToData(visit));
            pq.setVisitNumber(visit.getVisitNumber());
            patientQueue.add(pq);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(patientQueue, headers, HttpStatus.OK);
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

    /*
    @GetMapping("/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueue(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<PatientQueueData> page = patientQueueService.fetchQueue(pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }*/
    @GetMapping("/queue")
    public ResponseEntity<List<PatientQueueData>> fetchQueue(@RequestParam(value = "visitNumber", required = false) final String visitNumber, @RequestParam(value = "staffNumber", required = false) final String staffNumber, @RequestParam(value = "servicePoint", required = false) final String servicePoint, @RequestParam(value = "patientNumber", required = false) final String patientNumber, Pageable pageable) {
        Page<PatientQueueData> page = patientQueueService.fetchQueue(visitNumber, staffNumber, servicePoint, patientNumber, pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @PostMapping("/queue/{queueNo}/deactivate-queue")
    public ResponseEntity<?> deactivateFromQueue(@PathVariable("queueNo") final Long queueNo) {
        PatientQueue pq = patientQueueService.fetchQueueByID(queueNo);
        pq.setStatus(false);
        patientQueueService.createPatientQueue(pq);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
