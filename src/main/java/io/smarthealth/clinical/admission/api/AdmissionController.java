/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.api;

import io.smarthealth.clinical.admission.data.AdmissionData;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.service.AdmissionService;
import io.smarthealth.clinical.admission.service.BedService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
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
    private final BedService bedService;
    private final AuditTrailService auditTrailService; 

    @PostMapping("/admission")
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> createAdmission(@Valid @RequestBody AdmissionData admissionData) {

        Admission a = admissionService.createAdmission(admissionData);

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission request successfully submitted");
        pagers.setContent(AdmissionData.map(a));
        auditTrailService.saveAuditTrail("Admission", "Admitted patient "+a.getPatient().getFullName()+" to ward "+a.getWard().getName()+" ,room"+a.getRoom().getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/admission/{id}")
//    @PreAuthorize("hasAuthority('view_admission')")
    public ResponseEntity<?> findAdmissionById(
            @PathVariable("id") final Long id
    ) {
         auditTrailService.saveAuditTrail("Admission", "Searched Admission identified by id "+id);
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
            @RequestParam(value = "admissionNumber", required = false) final String admissionNo,
            @RequestParam(value = "wardId", required = false) final Long wardId,
            @RequestParam(value = "roomId", required = false) final Long roomId,
            @RequestParam(value = "bedId", required = false) final Long bedId,
            @RequestParam(value = "discharged", required = false) final Boolean discharged,
            @RequestParam(value = "activeVisit", required = false) final Boolean active,
            @RequestParam(value = "status", required = false) final Status status,
            @RequestParam(value = "q", required = false) final String term,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.createPage(page, size);
        final DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<AdmissionData> list = admissionService.fetchAdmissions(admissionNo, wardId, roomId, bedId, term, discharged, active, status, range, pageable).map(a -> AdmissionData.map(a));
        auditTrailService.saveAuditTrail("Admission", "Viewed all Admissions");
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
        auditTrailService.saveAuditTrail("Admission", "Edited Admissions idenfied by id "+id);
        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission Updated successfully");
        pagers.setContent(AdmissionData.map(a));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }


    @PutMapping("/admission/{admissionNo}/checkout")
//    @PreAuthorize("hasAuthority('create_admission')")
    public ResponseEntity<?> checkOuInPatient(@PathVariable("admissionNo") String admissionNo) {

        Admission a = admissionService.findAdmissionByNumber(admissionNo);
        if(!a.getDischarged()){
            throw APIException.badRequest("You cannot checkout a patient who has not been discharged!");
        }
        a.setStatus(Status.CheckOut);
        auditTrailService.saveAuditTrail("Admission", "Checkedout patient  "+a.getPatient().getFullName());
        admissionService.saveAdmission(a);

        //release bed
        Bed bed = a.getBed();
        bed.setStatus(Bed.Status.Available);
        bedService.updateBed(bed);

        Pager<AdmissionData> pagers = new Pager();
        pagers.setCode("200");
        pagers.setMessage("Admission Updated successfully");

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }
    @GetMapping("/admission/{admissionNo}/details")
    public ResponseEntity<AdmissionData> getAdmission(@PathVariable("admissionNo") String admissionNo){
        Optional<Admission> admission =admissionService.findByAdmissionNo(admissionNo);
        AdmissionData data=null;
        if(admission.isPresent()){
            data = AdmissionData.map(admission.get());
        }
        return ResponseEntity.ok(data);
    }

}
