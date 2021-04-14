/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.smarthealth.clinical.record.data.PatientDiagnosisData;
import io.smarthealth.security.service.AuditTrailService;

/**
 *
 * @author Kelsas
 */

@Api
@RestController
@RequestMapping("/api")
public class PatientDiagnosisController {
    private final DiagnosisService service;
    private final AuditTrailService auditTrailService;

    public PatientDiagnosisController(DiagnosisService service, AuditTrailService auditTrailService) {
        this.service = service;
        this.auditTrailService = auditTrailService;
    }
    
    @GetMapping("/patient-diagnosis") 
    public ResponseEntity<Pager<PatientDiagnosisData>> getVisitList(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) final Integer pageNo,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize
    ) {
        Pageable pageable = PaginationUtil.createPage(pageNo, pageSize);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<PatientDiagnosisData> page = service.fetchAllDiagnosis(visitNumber, patientNumber, range, pageable)
                .map(PatientDiagnosisData::map);
        auditTrailService.saveAuditTrail("Consultation", "Viewed all patient diagnosis");
        return ResponseEntity.ok((Pager<PatientDiagnosisData>) PaginationUtil.toPager(page, "Patient Diagnosis"));
    }

    @DeleteMapping("/patient-diagnosis/{id}")
    public ResponseEntity<?> deleteDiagnosis(@PathVariable("id") final Long id){
        service.deleteDiagnosis(id);
        return ResponseEntity.accepted().build();
    }
}
