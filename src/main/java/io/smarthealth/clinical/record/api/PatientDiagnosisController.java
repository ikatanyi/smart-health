/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.admission.data.DischargeDiagnosis;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import io.smarthealth.clinical.record.service.DiagnosisService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class PatientDiagnosisController {

    private final DiagnosisService service;
    
    @GetMapping("/patient-diagnosis")
//    @PreAuthorize("hasAuthority('view_discharge-summary')")
    public ResponseEntity<Pager<List<DischargeDiagnosis>>> getDiagnosis(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "gender", required = false) final Gender gender,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);

        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<DischargeDiagnosis> list = service.getPatientDiagnosis(visitNumber, patientNo, range, gender, pageable)
                .map(PatientDiagnosis::toData);

        Pager<List<DischargeDiagnosis>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Patient Diagnosis");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
}
