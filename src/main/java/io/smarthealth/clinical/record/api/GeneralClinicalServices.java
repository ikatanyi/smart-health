/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.clinical.record.data.ReferralData;
import io.smarthealth.clinical.record.data.SickOffNoteData;
import io.smarthealth.clinical.record.data.enums.ReferralType;
import io.smarthealth.clinical.record.domain.Referrals;
import io.smarthealth.clinical.record.domain.SickOffNote;
import io.smarthealth.clinical.record.service.ReferralsService;
import io.smarthealth.clinical.record.service.SickOffNoteService;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Simon.waweru
 */
@RestController
@RequestMapping("/api")
@Api(value = "General Clinical Services", description = "Operations pertaining to patient general services in a health facility")
public class GeneralClinicalServices {

    @Autowired
    VisitService visitService;

    @Autowired
    PatientService patientService;

    @Autowired
    SickOffNoteService sickOffNoteService;

    @Autowired
    ReferralsService referralsService;

    @Autowired
    EmployeeService employeeService;

//    prepare sick-off note
    @PostMapping("/sick-off")
    public @ResponseBody
    ResponseEntity<?> saveSickOffNote(@Valid @RequestBody SickOffNoteData sod) {
        Visit visit = visitService.findVisitEntityOrThrow(sod.getVisitNo());
        SickOffNote note = SickOffNoteData.map(sod);
        note.setVisit(visit);
        note.setPatient(visit.getPatient());
        SickOffNote son = sickOffNoteService.createSickOff(note);
        SickOffNoteData data = SickOffNoteData.map(son);
        Pager<SickOffNoteData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient Sick-Off Note");
        pagers.setContent(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @PostMapping("/visits/{visitNo}/sick-off")
    public @ResponseBody
    ResponseEntity<?> fetchSickOffNoteByVisit(@PathVariable("visitNo") final String visitNo) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        SickOffNoteData note = SickOffNoteData.map(sickOffNoteService.fetchSickNoteByVisitWithNotFoundThrow(visit));
        Pager<SickOffNoteData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient Sick-Off Note");
        pagers.setContent(note);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/patients/{patientNo}/sick-off")
    public @ResponseBody
    ResponseEntity<?> fetchSickOffNoteByPatient(@PathVariable("patientNo") final String patientNo, final Pageable pageable) {
        Patient patient = patientService.findPatientOrThrow(patientNo);
        Page<SickOffNoteData> page = sickOffNoteService.fetchSickOffNoteByPatient(patient, pageable).map(n -> SickOffNoteData.map(n));
        Pager<List<SickOffNoteData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Patient Sick-Off Notes");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    //referral 
    @PostMapping("/referrals")
    public @ResponseBody
    ResponseEntity<?> saveReferral(@Valid @RequestBody ReferralData rd) {
        Visit visit = visitService.findVisitEntityOrThrow(rd.getVisitNo());
        Referrals rde = ReferralData.map(rd);
        rde.setVisit(visit);
        rde.setPatient(visit.getPatient());
        if (rd.getReferralType().equals(ReferralType.Internal)) {
            Employee staff = employeeService.fetchEmployeeByNumberOrThrow(rd.getStaffNumber());
            rde.setDoctor(staff);
            rde.setDoctorName(staff.getFullName());
        }
        Referrals srd = referralsService.createReferrals(rde);
        if (rd.getReferralType().equals(ReferralType.External)) {
            visit.setStatus(VisitEnum.Status.Transferred);
            visitService.createAVisit(visit);
        }
        ReferralData data = ReferralData.map(srd);
        Pager<ReferralData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient referral request successfully submitted");
        pagers.setContent(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/visits/{visitNo}/referrals")
    public @ResponseBody
    ResponseEntity<?> fetchReferralsByVisit(@PathVariable("visitNo") final String visitNo) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        ReferralData note = ReferralData.map(referralsService.fetchReferalByVisitOrThrowIfNotFound(visit));
        Pager<ReferralData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Patient Referral Details");
        pagers.setContent(note);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
