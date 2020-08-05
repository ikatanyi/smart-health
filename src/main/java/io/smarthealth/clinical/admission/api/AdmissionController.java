/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.service.AdmissionService;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@Api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    @PostMapping("/admission")
    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> createAdmission() {

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Care Team successfully submitted");
        pagers.setContent(ct);

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
}
