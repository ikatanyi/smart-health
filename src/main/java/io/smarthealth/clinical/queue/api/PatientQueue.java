/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.queue.api;

import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.service.DepartmentService;
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
public class PatientQueue {

    @Autowired
    PatientService patientService;

    @Autowired
    PatientQueueService patientQueueService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/patientqueue/{deptId}")
    public ResponseEntity<List<PatientQueueData>> fetchQueuesByDepartment(@PathVariable("deptId") final Long deptId, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Department department = departmentService.fetchDepartmentById(deptId);
        Page<PatientQueueData> page = patientQueueService.fetchQueueByDept(department, pageable).map(q -> patientQueueService.convertToPatientQueueData(q));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
