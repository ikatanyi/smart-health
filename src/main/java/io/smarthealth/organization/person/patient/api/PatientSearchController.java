/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.api;

import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kelsas
 */
@Api
@RestController
@RequestMapping("/api")
public class PatientSearchController {

//    private final PatientSearchService patientSearchService;
    private final PatientService patientService;
    private final AuditTrailService auditTrailService;

    public PatientSearchController(PatientService patientService, AuditTrailService auditTrailService) {
        this.patientService = patientService;
        this.auditTrailService = auditTrailService;
    }

//    public PatientSearchController(PatientSearchService patientSearchService, PatientService patientService) {
//        this.patientSearchService = patientSearchService;
//        this.patientService = patientService;
//    }
//    @GetMapping("/patients/search")
//    public ResponseEntity<List<PatientData>> fetchAllPatients(@RequestParam(value = "q", required = false) final String searchTerm) {
//        List<PatientData> lists = patientSearchService.patientSearch(searchTerm).stream()
//                .map(p -> patientService.convertToPatientData(p))
//                .collect(Collectors.toList());
//        
//        return ResponseEntity.ok(lists);
//    }
    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientData>> fetchAllPatients(
            @RequestParam(value = "q", required = false) final String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size) {
        List<PatientData> lists = patientService.search(searchTerm, page, size).stream()
                .map(p -> patientService.convertToPatientData(p))
                .collect(Collectors.toList());
        auditTrailService.saveAuditTrail("Patient", "Viewed all registered patients");
        return ResponseEntity.ok(lists);
    }

}
