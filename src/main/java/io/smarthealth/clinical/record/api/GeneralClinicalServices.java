/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorItemService;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
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
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    BillingService billingService;

    @Autowired
    PricelistService pricelistService;

    @Autowired
    ServicePointService servicePointService;

    @Autowired
    DoctorItemService doctorItemService;

//    prepare sick-off note
    @PostMapping("/sick-off")
    @PreAuthorize("hasAuthority('create_clinicalservice')")
    public @ResponseBody
    ResponseEntity<?> saveSickOffNote(@Valid @RequestBody SickOffNoteData sod) {
        Visit visit = visitService.findVisitEntityOrThrow(sod.getVisitNo());
        //check if sick off note already exists
        Optional<SickOffNote> so = sickOffNoteService.fetchSickNoteByVisit(visit);
        if (so.isPresent()) {
            throw APIException.conflict("Sick off note already exists", sod.getVisitNo());
        }
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
    @PreAuthorize("hasAuthority('create_clinicalservice')")
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
    @PreAuthorize("hasAuthority('view_clinicalservice')")
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
    @PreAuthorize("hasAuthority('create_clinicalservice')")
    public @ResponseBody
    ResponseEntity<?> saveReferral(@Valid @RequestBody ReferralData rd) {
        Visit visit = visitService.findVisitEntityOrThrow(rd.getVisitNo());
        //find referral by visit
//        Optional<Referrals> ref = referralsService.fetchReferalByVisit(visit);
//        if (ref.isPresent()) {
//            throw APIException.conflict("Referral on this visit has already been created", rd.getVisitNo());
//        }
        Patient patient = visit.getPatient();
        Referrals rde = ReferralData.map(rd);
        rde.setVisit(visit);
        rde.setPatient(visit.getPatient());
        if (rd.getReferralType().equals(ReferralType.Internal)) {
            Employee employee = employeeService.fetchEmployeeByNumberOrThrow(rd.getStaffNumber());
            rde.setDoctor(employee);
            rde.setDoctorName(employee.getFullName());
            //create bill for the referred doctor
            //find consultation service by doctor selected
            if (rd.getDoctorServiceId() != null) {
                ServicePoint sp = servicePointService.getServicePointByType(ServicePointType.Consultation);
                DoctorItem docService = doctorItemService.getDoctorItem(rd.getDoctorServiceId());
                PriceList pricelist = pricelistService.fetchPriceListByItemAndServicePoint(docService.getServiceType(), sp);
                List<BillItemData> billItems = new ArrayList<>();
                BillItemData itemData = new BillItemData();
                itemData.setAmount(pricelist.getSellingRate().doubleValue());
                itemData.setBalance(pricelist.getSellingRate().doubleValue());
                itemData.setBillingDate(LocalDate.now());
                itemData.setItem(pricelist.getItem().getItemName());
                itemData.setItemCode(pricelist.getItem().getItemCode());
                if (employee != null) {
                    itemData.setMedicId(employee.getId());
                    itemData.setMedicName(employee.getFullName());
                }
                itemData.setQuantity(1.0);
                itemData.setServicePoint(visit.getServicePoint().getName());
                itemData.setServicePointId(visit.getServicePoint().getId());
                billItems.add(itemData);

                BillData data = new BillData();
                data.setBillItems(billItems);
                data.setAmount(pricelist.getSellingRate().doubleValue());
                data.setBalance(pricelist.getSellingRate().doubleValue());
                data.setBillingDate(LocalDate.now());
                data.setDiscount(0.00);
                data.setPatientName(patient.getFullName());
                data.setPatientNumber(patient.getPatientNumber());
                data.setPaymentMode(visit.getPaymentMethod().name());
                data.setVisitNumber(visit.getVisitNumber());
                data.setWalkinFlag(Boolean.FALSE);
                billingService.createPatientBill(data);
            }
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
    @PreAuthorize("hasAuthority('view_clinicalservice')")
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

    @GetMapping("/visits/requested-practioners")
    @PreAuthorize("hasAuthority('view_clinicalservice')")
    public @ResponseBody
    ResponseEntity<?> fetchDistinctPractionersRequestedByActiveVisits() {
        List<Employee> empList = visitService.practionersByActiveVisits();
        List<EmployeeData> employeeData = new ArrayList<>();
        for (Employee e : empList) {
            EmployeeData d = new EmployeeData();
            d.setText(e.getFullName());
            d.setValue(e.getFullName());
            d.setFullName(e.getFullName());
            d.setStaffNumber(e.getStaffNumber());
            d.setSpecialization(e.getSpecialization());
            employeeData.add(d);
        }
        Pager<List<EmployeeData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(employeeData);
        PageDetails details = new PageDetails();
        details.setPage(employeeData.size());
        details.setPerPage(1);
        details.setTotalElements(Long.valueOf(employeeData.size()));
        details.setTotalPage(1);
        details.setReportName("Employee data");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    @GetMapping("/referrals")
    @PreAuthorize("hasAuthority('view_clinicalservice')")
    public @ResponseBody
    ResponseEntity<?> fetchReferrals(
            @RequestParam(value = "visitNo", required = false) final String visitNo,
            @RequestParam(value = "patientNo", required = false) final String patientNo,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize,
            @RequestParam(value = "pageNo", required = false) final Integer pageNo
    ) {
        Pageable pageable = Pageable.unpaged();
        if (pageNo != null && pageSize != null) {
            pageable = PageRequest.of(pageNo, pageSize);
        }
        Page<ReferralData> page = referralsService.fetchReferrals(visitNo, patientNo, pageable).map((r) -> ReferralData.map(r));

        Pager< List< ReferralData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Patient Referrals");
        pagers.setPageDetails(details);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

}
