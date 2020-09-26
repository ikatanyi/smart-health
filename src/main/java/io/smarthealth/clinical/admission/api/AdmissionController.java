/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.service.AdmissionService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> createAdmission(@Valid @RequestBody AdmissionData admissionData) {

        Admission a = admissionService.createAdmission(admissionData);

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission request successfully submitted");
        pagers.setContent(AdmissionData.map(a));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/admission/{id}")
//    @PreAuthorize("hasAuthority('view_admission')")
    public ResponseEntity<?> findAdmissionById(
            @PathVariable("id") final Long id
    ) {

        Admission a = admissionService.findAdmissionById(id);

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission Data");
        pagers.setContent(AdmissionData.map(a));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/admission")
//    @PreAuthorize("hasAuthority('view_admission')")
    public ResponseEntity<?> getAdmission(
            @RequestParam(value = "admissionNo", required = false) final String admissionNo,
            @RequestParam(value = "wardId", required = false) final Long wardId,
            @RequestParam(value = "roomId", required = false) final Long roomId,
            @RequestParam(value = "bedId", required = false) final Long bedId,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<AdmissionData> list = admissionService.fetchAdmissions(admissionNo, wardId, roomId, bedId, term, pageable).map(a -> AdmissionData.map(a));

        Pager<List<AdmissionData>> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Admission Data");
        pagers.setPageDetails(details);

        return ResponseEntity.ok(pagers);
    }
    
    @PutMapping("/admission/{id}")
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> updateAdmission(@PathVariable("id") Long id, @Valid @RequestBody AdmissionData admissionData) {

        Admission a = admissionService.updateAdmission(id, admissionData);

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission Updated successfully");
        pagers.setContent(AdmissionData.map(a));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }
    
    

}
