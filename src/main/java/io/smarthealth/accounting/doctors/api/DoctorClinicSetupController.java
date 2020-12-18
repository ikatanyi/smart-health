/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.doctors.api;

import io.smarthealth.accounting.doctors.data.DoctorClinicData;
import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import io.smarthealth.accounting.doctors.service.DoctorClinicService;
import io.smarthealth.administration.employeespecialization.service.EmployeeSpecializationService;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class DoctorClinicSetupController {

    @Autowired
    DoctorClinicService doctorClinicService;

    @Autowired
    EmployeeSpecializationService specializationService;

    @Autowired
    ItemService itemService;
    
    @Autowired
    AuditTrailService auditTrailService;

    @PostMapping("/clinics")
    @PreAuthorize("hasAuthority('create_doctorClinic')")
    public ResponseEntity<?> createDoctorClinic(@Valid @RequestBody DoctorClinicData data) {
//find
        Item item = itemService.findItemEntityOrThrow(data.getServiceId());

        DoctorClinicItems clinic = new DoctorClinicItems();
        clinic.setClinicName(data.getClinicName());
        clinic.setServiceType(item);
        if (data.getHasReviewCost()) {
            clinic.setHasReviewCost(Boolean.TRUE);
            clinic.setReviewService(itemService.findItemEntityOrThrow(data.getReviewServiceId()));
        } else {
            clinic.setHasReviewCost(Boolean.FALSE);
        }

        DoctorClinicItems savedClinic = doctorClinicService.saveClinicItem(clinic);
        auditTrailService.saveAuditTrail("Clinics", "Created a doctor clinic "+savedClinic.getClinicName());
        Pager<DoctorClinicData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Clinic Created.");
        pagers.setContent(DoctorClinicData.map(savedClinic));

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/clinics")
    @PreAuthorize("hasAuthority('view_doctorClinic')")
    public ResponseEntity<?> getDoctorInvoices(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer size) {

        Pageable pageable = PaginationUtil.createPage(page, size);
        Page<DoctorClinicData> list = doctorClinicService.fetchClinics(pageable).map(c -> DoctorClinicData.map(c));
        auditTrailService.saveAuditTrail("Clinics", "Viewed all doctor clinics ");
        Pager<List<DoctorClinicData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(list.getContent());
        PageDetails details = new PageDetails();
        details.setPage(list.getNumber() + 1);
        details.setPerPage(list.getSize());
        details.setTotalElements(list.getTotalElements());
        details.setTotalPage(list.getTotalPages());
        details.setReportName("Doctors Clinics");
        pagers.setPageDetails(details);
        return ResponseEntity.ok(pagers);
    }

    @GetMapping("/clinics/{id}")
    @PreAuthorize("hasAuthority('view_doctorClinic')")
    public ResponseEntity<?> fetchDoctorClinicById(@PathVariable("id") final Long clinicId) {

        DoctorClinicItems clinic = doctorClinicService.fetchClinicById(clinicId);

        Pager<DoctorClinicData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Clinic Details");
        pagers.setContent(DoctorClinicData.map(clinic));
        auditTrailService.saveAuditTrail("Clinics", "Viewed  doctor clinic with id "+clinicId);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PutMapping("/clinics/{id}")
    @PreAuthorize("hasAuthority('view_doctorClinic')")
    public ResponseEntity<?> updateDoctorClinicById(
            @PathVariable("id") final Long clinicId,
            @Valid @RequestBody DoctorClinicData data
    ) {

        DoctorClinicItems clinic = doctorClinicService.fetchClinicById(clinicId);
        Item item = itemService.findItemEntityOrThrow(data.getServiceId());
        clinic.setClinicName(data.getClinicName());
        clinic.setServiceType(item);
        if (data.getHasReviewCost()) {
            clinic.setHasReviewCost(Boolean.TRUE);
            clinic.setReviewService(itemService.findItemEntityOrThrow(data.getReviewServiceId()));
        } else {
            clinic.setHasReviewCost(Boolean.FALSE);
            clinic.setReviewService(null);
        }

        DoctorClinicItems savedClinic = doctorClinicService.saveClinicItem(clinic);
        auditTrailService.saveAuditTrail("Clinics", "Edited  doctor clinic  "+clinic.getClinicName());
        Pager<DoctorClinicData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Clinic Updated.");
        pagers.setContent(DoctorClinicData.map(savedClinic));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
