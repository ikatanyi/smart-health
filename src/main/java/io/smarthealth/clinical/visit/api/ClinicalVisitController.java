package io.smarthealth.clinical.visit.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.data.CopayData;
import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillRepository;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.doctors.domain.DoctorItem;
import io.smarthealth.accounting.doctors.service.DoctorClinicService;
import io.smarthealth.accounting.doctors.service.DoctorInvoiceService;
import io.smarthealth.accounting.pricelist.domain.PriceBook;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.service.AdmissionService;
import io.smarthealth.clinical.admission.service.BedService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.TriageNotesData;
import io.smarthealth.clinical.record.data.VitalRecordData;
import io.smarthealth.clinical.record.domain.TriageNotes;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.record.service.TriageNotesService;
import io.smarthealth.clinical.triage.service.TriageService;
import io.smarthealth.clinical.visit.data.PaymentDetailsData;
import io.smarthealth.clinical.visit.data.SpecialistChangeAuditData;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
//import io.smarthealth.clinical.visit.data.enums.TriageCategory;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.PaymentDetailAudit;
import io.smarthealth.clinical.visit.domain.PaymentDetailAuditRepository;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.SpecialistChangeAuditService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.security.domain.User;
import io.smarthealth.security.service.UserService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.security.service.AuditTrailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.codehaus.jettison.json.JSONException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import io.smarthealth.clinical.visit.data.SimpleVisit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;

/**
 * @author Kelsas
 */
@RestController
@RequestMapping("/api")
@Api(value = "Patient Visit", description = "Operations pertaining to patient visit in a health facility")
public class ClinicalVisitController {

    @Autowired
    private VisitService visitService;

    @Autowired
    PatientService patientService;
    @Autowired
    TriageService triageService;

    @Autowired
    PatientQueueService patientQueueService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    ServicePointService servicePointService;

    @Autowired
    SchemeService schemeService;

    @Autowired
    PaymentDetailsService paymentDetailsService;

    @Autowired
    TriageNotesService triageNotesService;

    @Autowired
    private SequenceNumberService sequenceNumberService;

    @Autowired
    BillingService billingService;

    @Autowired
    ItemService itemService;

    @Autowired
    DoctorClinicService clinicService;

    @Autowired
    PricelistService pricelistService;

    @Autowired
    DoctorInvoiceService doctorInvoiceService;

    @Autowired
    PaymentDetailAuditRepository paymentDetailAuditRepository;

    @Autowired
    private PatientBillRepository patientBillRepository;

    @Autowired
    AuditTrailService auditTrailService;

    @Autowired
    private SpecialistChangeAuditService specialistChangeAuditService;

    @Autowired
    UserService service;

    @Autowired
    AdmissionService admissionService;

    @Autowired
    BedService bedService;

    @PostMapping("/visits")
    @PreAuthorize("hasAuthority('create_visits')")
    @ApiOperation(value = "Submit a new patient visit", response = VisitData.class)
    @Transactional(rollbackFor = Exception.class)
    public @ResponseBody
    ResponseEntity<?> addVisitRecord(@RequestBody @Valid final VisitData visitData) {

        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        //check if patient has an active visit
        if (visitService.isPatientVisitActive(patient)) {
            throw APIException.conflict("Patient identified by {0} already has an active visit", patient.getPatientNumber());
        }

        Visit visit = VisitData.map(visitData);
        Employee employee = null;
        if (visitData.getPractitionerCode() != null) {
            employee = employeeService.fetchEmployeeByNumberOrThrow(visitData.getPractitionerCode());
            visit.setHealthProvider(employee);
        }
        ServicePoint servicePoint = servicePointService.getServicePoint(visitData.getLocationIdentity());

        //generate visit number
        String visitNo = sequenceNumberService.next(1L, Sequences.Visit.name());
        visit.setVisitNumber(visitNo);
        visit.setStartDatetime(visitData.getStartDatetime());
        visit.setPatient(patient);
        visit.setServicePoint(servicePoint);
        visit = this.visitService.createAVisit(visit);

        //register payment details 
        Scheme scheme = null;
        if (visitData.getPaymentMethod().equals(PaymentMethod.Insurance)) {
            scheme = schemeService.fetchSchemeById(visitData.getPayment().getSchemeId());
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
            PaymentDetails pd = PaymentDetailsData.map(visitData.getPayment());

            pd.setScheme(scheme);
            pd.setPayer(scheme.getPayer());
            pd.setVisit(visit);
            if (config.isPresent()) {
                SchemeConfigurations conf = config.get();
                pd.setCoPayCalcMethod(conf.getCoPayType());
                pd.setCoPayValue(conf.getCoPayValue());
                pd.setHasCapitation(conf.isCapitationEnabled());
                pd.setCapitationAmount(conf.getCapitationAmount());
            }
            pd.setRunningLimit(visitData.getPayment().getLimitAmount());
            pd.setPatient(patient);
            paymentDetailsService.createPaymentDetails(pd);
            //create bill for copay
            //Modification - reusing copayment billing (kelsas)
            //TODO disable copay from being billed at start
            if (config.isPresent() && config.get().getCoPayValue() > 0) {
                billingService.createCopay(new CopayData(visit.getVisitNumber(), visitData.getPayment().getSchemeId()));
            }
        }
        //Push it to queue
        PatientQueue patientQueue = new PatientQueue();
        patientQueue.setServicePoint(servicePoint);
        patientQueue.setPatient(patient);
        patientQueue.setStatus(true);
        patientQueue.setVisit(visit);
        patientQueueService.createPatientQueue(patientQueue);
        //create bill if consultation
        if (!visit.getServiceType().equals(VisitEnum.ServiceType.Other)) {
            Long schemeId = scheme != null ? scheme.getId() : null;
            createConsultationBill(visit, visitData.getItemToBill(), employee, schemeId);
        }
        //update visit
        visit = visitService.createAVisit(visit);
        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}")
                .buildAndExpand(visit.getVisitNumber()).toUri();
        auditTrailService.saveAuditTrail("Visit", "Activated a visit for patient " + visit.getPatient().getFullName());
        return ResponseEntity.created(location).body(ApiResponse.successMessage("Visit was activated successfully", HttpStatus.CREATED, visitDat));
    }

    private void createConsultationBill(Visit visit, Long clinicId, Employee doctor, Long schemeId) throws APIException {
        ServicePoint sp = servicePointService.getServicePointByType(ServicePointType.Consultation);
        Patient patient = visit.getPatient();
        if (sp == null) {
            throw APIException.notFound("Consultation service point not found");
        }

        if (visit.getServicePoint().getServicePointType().equals(ServicePointType.Consultation)) {
            visit.setIsActiveOnConsultation(Boolean.TRUE);
        } else {
            visit.setIsActiveOnConsultation(Boolean.FALSE);
        }
        DoctorClinicItems clinic = clinicService.fetchClinicById(clinicId);
        visit.setClinic(clinic);
        //PriceList pricelist = pricelistService.fetchPriceListByItemAndPriceBook(clinic.getServiceType(), null);
        PriceBook pb = null;
        //find pricebook
        if (visit.getPaymentMethod().equals(PaymentMethod.Insurance)) {
            //get the scheme
            if (schemeId != null) {
                Scheme scheme = schemeService.fetchSchemeById(schemeId);
                pb = Optional.of(scheme.getPayer().getPriceBook()).orElse(null);
            }
        }

        //TODO: use pricelist not item service i.e fetchPriceListByItemAndPriceBook
        Item item = visit.getServiceType().equals(VisitEnum.ServiceType.Consultation) ? clinic.getServiceType() : clinic.getHasReviewCost() ? clinic.getReviewService() : null;
        if (item != null) {
            double sellimgPrice = pricelistService.fetchPriceAmountByItemAndPriceBook(item, pb);
            List<BillItemData> billItems = new ArrayList<>();
            BillItemData itemData = new BillItemData();
            itemData.setAmount(sellimgPrice);
            itemData.setBalance(sellimgPrice);
            itemData.setBillingDate(LocalDate.now());
            itemData.setPrice(sellimgPrice);
            itemData.setItem(item.getItemName());
            itemData.setItemCode(item.getItemCode());
            if (doctor != null) {
                itemData.setMedicId(doctor.getId());
                itemData.setMedicName(doctor.getFullName());
            }
            itemData.setQuantity(1.0);
            itemData.setServicePoint(sp.getName());
            itemData.setServicePointId(sp.getId());
            itemData.setPaymentMethod(visit.getPaymentMethod());
            billItems.add(itemData);

            BillData data = new BillData();
            data.setWalkinFlag(false);
            data.setBillItems(billItems);
            data.setAmount(sellimgPrice);
            data.setBalance(sellimgPrice);
            data.setBillingDate(LocalDate.now());
            data.setDiscount(0.00);
            data.setPatientName(patient.getFullName());
            data.setPatientNumber(patient.getPatientNumber());
            data.setPaymentMode(visit.getPaymentMethod().name());
            data.setVisitNumber(visit.getVisitNumber());
            auditTrailService.saveAuditTrail("Visit", "Created a Consultation bill for patient " + patient.getFullName());
            billingService.createPatientBill(data);
        }
    }

    @PutMapping("/visits/{visitNumber}")
    @PreAuthorize("hasAuthority('edit_visits')")
    @ApiOperation(value = "Update patient visit record", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitRecord(@PathVariable("visitNumber") final String visitNumber,
                                        @RequestBody
                                        @Valid final VisitData visitData
    ) {
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        visit.setScheduled(visitData.getScheduled());
        visit.setStartDatetime(visitData.getStartDatetime());
        visit.setStopDatetime(visitData.getStopDatetime());
        visit.setVisitNumber(visitData.getVisitNumber());
        visit.setVisitType(visitData.getVisitType());
        visit.setStatus(visitData.getStatus());
        visit.setPatient(patient);
        visit = this.visitService.createAVisit(visit);
        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}")
                .buildAndExpand(visit.getVisitNumber()).toUri();
        auditTrailService.saveAuditTrail("Visit", "Edited a Patient visit for patient " + patient.getFullName());
        return ResponseEntity.created(location).body(visitDat);
    }

    @PutMapping("/visits/{visitNumber}/status/{status}")
    @PreAuthorize("hasAuthority('edit_visits')")
    @ApiOperation(value = "Update patient visit status", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitStatus(@PathVariable("visitNumber") final String visitNumber,
                                        @PathVariable("status") final String status
    ) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        if (status.equals(VisitEnum.Status.CheckOut.name()) && visit.getVisitType().equals(VisitEnum.VisitType.Inpatient) && !visit.getStatus().equals(VisitEnum.Status.Discharged)) {
            throw APIException.badRequest("Huh! This patient is not yet discharged!");
        }

        visit.setStatus(VisitEnum.Status.valueOf(status));
        visit = this.visitService.createAVisit(visit);
        //release bed
        if (visit.getVisitType().equals(VisitEnum.VisitType.Inpatient)) {
            Admission a = admissionService.findAdmissionByNumber(visitNumber);
            Bed bed = a.getBed();
            bed.setStatus(Bed.Status.Available);
            bedService.updateBed(bed);
        }

        if (status.equals(VisitEnum.Status.CheckOut.name())) {
            //mark active visit status on queue as false
            List<PatientQueue> pq = patientQueueService.fetchQueueByVisit(visit);
            for (PatientQueue q : pq) {
                q.setStatus(false);
                patientQueueService.createPatientQueue(q);
            }
        }

        //Convert to data
        VisitData visitDat = VisitData.map(visit);
        auditTrailService.saveAuditTrail("Visit", "Edited a Patient visit status for patient " + visit.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.OK).body(visitDat);
    }

    @PutMapping("/visits/{visitNumber}/consultation-status/{status}")
    @PreAuthorize("hasAuthority('edit_visits')")
    @ApiOperation(value = "Update patient visit consultation status", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateConsultationStatus(
            @PathVariable("visitNumber") final String visitNumber,
            @PathVariable("status") final Boolean status) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        visit.setIsActiveOnConsultation(status);
        visit = this.visitService.createAVisit(visit);
        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        Pager<VisitData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Update Successful");
        pagers.setContent(visitDat);
        auditTrailService.saveAuditTrail("Visit", "Edited a Patient consultation status for patient " + visit.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/visits/{visitNumber}")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "View patient visit", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> viewVisit(
            @PathVariable("visitNumber") final String visitNumber) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        PatientData patientData = patientService.convertToPatientData(visit.getPatient());
        visitDat.setPatientData(patientData);

        Pager<VisitData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Visit Data");
        pagers.setContent(visitDat);
        auditTrailService.saveAuditTrail("Visit", "viewed a Patient visit identified by visitNo " + visitNumber);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PutMapping("/visits/{visitNumber}/doctor/{staffNumber}")
    @PreAuthorize("hasAuthority('edit_visits')")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitPractitioner(
            @PathVariable("visitNumber") final String visitNumber,
            @PathVariable("staffNumber") final String staffNumber,
            @RequestParam(value = "reason", required = false) final String reason,
            @RequestParam(value = "clinic_id", required = false) final Long clinicId
    ) {
        Employee employee = employeeService.fetchEmployeeByNumberOrThrow(staffNumber);
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        updateVisitDoctor(visit, employee, reason, clinicId);

        visit.setHealthProvider(employee);

        visit = visitService.createAVisit(visit);

        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        Pager<VisitData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Update Successful");
        pagers.setContent(visitDat);
        auditTrailService.saveAuditTrail("Visit", "Edited a Patient consultation doctor for patient " + visit.getPatient().getFullName() + " to doctor " + employee.getFullName());
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/visits")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<List<VisitData>> fetchAllVisits(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "staffNumber", required = false) final String staffNumber,
            @RequestParam(value = "servicePointType", required = false) final ServicePointType servicePointType,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "patientName", required = false) final String patientName,
            @RequestParam(value = "runningStatus", required = false, defaultValue = "true") final boolean runningStatus,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "isActiveOnConsultation", required = false) final Boolean isActiveOnConsultation,
            @RequestParam(value = "orderByTriageCategory", required = false, defaultValue = "false") final Boolean orderByTriageCategory,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "term", required = false) final String queryTerm,
            @RequestParam(value = "pageNo", required = false) final Integer pageNo,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize,
            @RequestParam(value = "billPaymentValidation", required = false, defaultValue = "false") final Boolean billPaymentValidationPoint
    ) {
        Pageable pageable = Pageable.unpaged();

        if (pageNo != null && pageSize != null) {
            pageable = PageRequest.of(pageNo, pageSize);
        }
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<VisitData> page = visitService.fetchAllVisits(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, range, isActiveOnConsultation, username, orderByTriageCategory, queryTerm, billPaymentValidationPoint, pageable).map(v -> convertToVisitData(v));
        auditTrailService.saveAuditTrail("Visit", "Viewed all patients visits");
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping("/visits/{visitNo}/payment-mode")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<?> fetchpaymentModeByVisit(@PathVariable("visitNo") String visitNo) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        PaymentDetails pde = paymentDetailsService.fetchPaymentDetailsByVisit(visit);
        Pager<PaymentDetailsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Mode");
        pagers.setContent(PaymentDetailsData.map(pde));
        //auditTrailService.saveAuditTrail("Visit", "Viewed a Patient payment mode for patient visit "+visit.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/last-payment-mode/{patientNumber}")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<?> fetchpaymentModeByLastVisit(@PathVariable("patientNumber") String patientNumber) {
        Patient p = patientService.findPatientOrThrow(patientNumber);
        auditTrailService.saveAuditTrail("Visit", "Retrived a last payment mode for patient " + p.getFullName());
        Optional<PaymentDetails> pde = paymentDetailsService.getLastPaymentDetailsByPatient(p);
        Pager<PaymentDetailsData> pagers = new Pager();
        if (pde.isPresent()) {
            pagers.setCode("200");
            pagers.setMessage("Payment Mode");
            pagers.setContent(PaymentDetailsData.map(pde.get()));

        } else {
            pagers.setCode("404");
            pagers.setMessage("Payment Mode Not Found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PutMapping("/visits/{visitNo}/limit-amount")
    @PreAuthorize("hasAuthority('edit_visits')")
    public ResponseEntity<?> updateLimitAmount(@PathVariable("visitNo") String visitNo, @Valid @RequestBody PaymentDetailsData data, Authentication authentication) {
//find visit details by visitNo
        String username = authentication.getName();
        User user = service.findUserByUsernameOrEmail(username)
                .orElseThrow(() -> APIException.badRequest("User not found"));
        Optional<PaymentDetails> pd = paymentDetailsService.fetchPaymentDetailsByVisitWithoutNotFoundDetection(visitService.findVisitEntityOrThrow(visitNo));
        if (pd.isPresent()) {
            PaymentDetails pdd = pd.get();
            pdd.setExcessAmountAuthorisedBy(user);
            pdd.setExcessAmountEnabled(data.getExcessAmountEnabled());
            pdd.setExcessAmountPayMode(data.getPaymentMethod());
            if (data.getPaymentMethod().equals(PaymentMethod.Insurance)) {
//update excess card details

            }
            paymentDetailsService.createPaymentDetails(pdd);
            auditTrailService.saveAuditTrail("Visit", "Edited a Patient limit amount for patient visit " + visitNo);
        }
        return ResponseEntity.ok(true);
    }

    @PutMapping("/visits/{visitNo}/payment-mode")
    @PreAuthorize("hasAuthority('edit_visits')")
    public ResponseEntity<?> updatePaymentMode(@PathVariable("visitNo") String visitNo, @Valid @RequestBody PaymentDetailsData data) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);

        if (visit.getStatus() != VisitEnum.Status.CheckIn) {
            throw APIException.badRequest("Visit should be active to update the payment details");
        }
        //check the currnt
        Optional<PaymentDetails> currentPaymentDetail = paymentDetailsService.getPaymentDetailsByVist(visit);

        PaymentDetails paymentDetails = null;

        if (currentPaymentDetail.isPresent()) {
            PaymentDetails pd = currentPaymentDetail.get();

            PaymentDetailAudit audit = new PaymentDetailAudit();
            audit.setChangeDate(LocalDateTime.now());
            audit.setMemberName(pd.getMemberName());
            audit.setPayer(pd.getPayer());
            audit.setPolicyNo(pd.getPolicyNo());
            audit.setReason(data.getComments());
            audit.setRelation(pd.getRelation());
            audit.setScheme(pd.getScheme());
            audit.setVisit(pd.getVisit());

            paymentDetailAuditRepository.save(audit);

            if (data.getPaymentMethod() == PaymentMethod.Cash) {
                paymentDetailsService.deletePaymentDetails(pd);
            }

        }

        if (data.getPaymentMethod() == PaymentMethod.Cash) {
            visit.setPaymentMethod(PaymentMethod.Cash);
        } else {
            visit.setPaymentMethod(PaymentMethod.Insurance);
            if (data.getSchemeId() == null) {
                throw APIException.badRequest("Scheme Id is required");
            }
            Scheme scheme = schemeService.fetchSchemeById(data.getSchemeId());
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
            PaymentDetails pd = null;
            if (currentPaymentDetail.isPresent()) {
                pd = currentPaymentDetail.get();
            } else {
                pd = new PaymentDetails();
            }
            pd.setComments(data.getComments());
            pd.setPolicyNo(data.getPolicyNo());
            pd.setMemberName(data.getMemberName());
            pd.setScheme(scheme);
            pd.setPayer(scheme.getPayer());
            pd.setVisit(visit);
            pd.setPatient(visit.getPatient());
            pd.setRelation(data.getRelation());
            if (config.isPresent()) {
                pd.setCoPayCalcMethod(config.get().getCoPayType());
                pd.setCoPayValue(config.get().getCoPayValue());
            }
            paymentDetails = paymentDetailsService.createPaymentDetails(pd);

        }
        Visit updatedVisit = visitService.save(visit);

        //update those bills that have not been paid
        //
        List<PatientBill> bills = patientBillRepository.findByVisit(visit);
        List<PatientBill> updatedBills = bills.stream()
                .map(x -> {
                    x.setPaymentMode(updatedVisit.getPaymentMethod().name());
                    return x;
                })
                .collect(Collectors.toList());
        ;

        patientBillRepository.saveAll(updatedBills);

        PaymentDetailsData ppd = paymentDetails != null ? PaymentDetailsData.map(paymentDetails) : null;

        Pager<PaymentDetailsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Mode Changed Successful");
        pagers.setContent(ppd);
        auditTrailService.saveAuditTrail("Visit", "Edited a Patient payment mode for patient " + visit.getPatient().getFullName());
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/patients/{patientNumber}/active-visit")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<?> fetchActiveVisitByPatient(@PathVariable("patientNumber") String patientNumber
    ) {
        Patient patient = visitService.findPatientOrThrow(patientNumber);
        auditTrailService.saveAuditTrail("Visit", "Searched active visits for patient " + patient.getFullName());
        Optional<Visit> visit = visitService.fetchVisitByPatientAndStatus(patient, VisitEnum.Status.CheckIn);
        if (visit.isPresent()) {
            Pager<VisitData> pagers = new Pager();
            pagers.setCode("0");
            pagers.setMessage("Visit Data");
            pagers.setContent(convertToVisitData(visit.get()));
            return ResponseEntity.status(HttpStatus.OK).body(pagers);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIException.notFound("{0} does not have an active visit", patient.getFullName()));
            // throw APIException.notFound("{0} does not have an active visit", patient.getFullName());
        }

    }

    @GetMapping("/patients/{patientNumber}/visits")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<List<VisitData>> fetchAllVisitsByPatient(@PathVariable("patientNumber") final String patientNumber,
                                                                   @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
                                                                   Pageable pageable
    ) {
        //System.out.println("patientNumber " + patientNumber);
        Page<VisitData> page = visitService.fetchVisitByPatientNumber(patientNumber, pageable).map(v -> convertToVisitData(v));
        auditTrailService.saveAuditTrail("Visit", "Viewed all Patient visits for patient identified by " + patientNumber);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/visits/{visitNumber}/vitals")
    @PreAuthorize("hasAuthority('create_visits')")
    @ApiOperation(value = "Create/Add a new patient vital by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByVisit(@PathVariable("visitNumber") String visitNumber,
                                                          @RequestBody
                                                          @Valid final VitalRecordData vital
    ) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        VitalsRecord vitalR = this.triageService.addVitalRecordsByVisit(visit, vital);

//        VitalRecordData vr = modelMapper.map(vitalR, VitalRecordData.class);
        VitalRecordData vr = triageService.convertToVitalsData(vitalR);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}/vitals/{id}")
                .buildAndExpand(visitNumber, vitalR.getId()).toUri();
        auditTrailService.saveAuditTrail("Visit", "Entered Patient vitals for patient identified by visitNo " + visitNumber);
        return ResponseEntity.created(location).body(vr);
    }

    @PostMapping("/visits/{visitNumber}/triage-notes")
    @PreAuthorize("hasAuthority('create_visits')")
    @ApiOperation(value = "Create/Add a new patient triage notes by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> addTriageNotesByVisit(@PathVariable("visitNumber") String visitNumber,
                                            @RequestBody
                                            @Valid final TriageNotesData triageNotesData
    ) {
        final Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        TriageNotes e = TriageNotesData.map(triageNotesData);
        if (visit.getPatient().getGender().equals(Gender.M)) {
            e.setLMP(null);
        }
        e.setVisit(visit);

        Pager<TriageNotesData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Triage notes has successfully been saved");
        pagers.setContent(TriageNotesData.map(triageNotesService.createNewTriageNotes(e)));
        auditTrailService.saveAuditTrail("Visit", "Entered Patient triage notes for patient identified by visitNo " + visitNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/visits/{visitNumber}/triage-notes")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Create/Add a new patient triage notes by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> fetchTriageNotesByVisit(@PathVariable("visitNumber") String visitNumber, final Pageable pageable
    ) {
        final Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        Page<TriageNotesData> page = triageNotesService.fethAllTriageNotesByVisit(visit, pageable).map(n -> TriageNotesData.map(n));
        Pager<List<TriageNotesData>> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Success");
        pagers.setContent(page.getContent());
        PageDetails details = new PageDetails();
        details.setPage(page.getNumber());
        details.setPerPage(page.getSize());
        details.setTotalElements(page.getTotalElements());
        details.setTotalPage(page.getTotalPages());
        details.setReportName("Triage Notes ");
        pagers.setPageDetails(details);
        auditTrailService.saveAuditTrail("Visit", "viewed Patient triage notes for patient identified by visitNo " + visitNumber);
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    @GetMapping("/triage-notes/{id}")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch triage notes by id", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> findTriageNotesById(@PathVariable("v") final Long id
    ) {
        TriageNotes e = triageNotesService.fetchTriageNoteById(id);
        Pager<TriageNotesData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Triage notes");
        pagers.setContent(TriageNotesData.map(e));
        auditTrailService.saveAuditTrail("Visit", "Searched Patient triage notes for patient identified by id " + id);
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/patients/{patientNumber}/last-visit")
    @PreAuthorize("hasAuthority('view_visits')")
    @ApiOperation(value = "Fetch all patient's last vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<?> fetchLastVisit(
            @PathVariable("patientNumber") final String patientNumber,
            @RequestParam(value = "currentVisitNumber", required = false) final String currentVisitNumber
    ) {

        Page<Visit> v = visitService.lastVisit(patientService.findPatientOrThrow(patientNumber), currentVisitNumber);
        auditTrailService.saveAuditTrail("Visit", "Viewed Last visit Patient vitals for patient identified by patientNo " + patientNumber);
        if (v.getContent().size() > 0) {
            Pager<VisitData> pagers = new Pager();
            pagers.setCode("0");
            pagers.setMessage("Last Visit Data");
            pagers.setContent(VisitData.map(v.getContent().get(0)));
            return ResponseEntity.status(HttpStatus.OK).body(pagers);
        } else {
            Pager<VisitData> pagers = new Pager();
            pagers.setCode("0");
            pagers.setMessage("Last Visit Data");
            pagers.setContent(new VisitData());
            return ResponseEntity.status(HttpStatus.OK).body(pagers);
        }

    }

    @PostMapping("/patient/{patientNo}/vitals")
    @PreAuthorize("hasAuthority('create_visits')")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByPatient(@PathVariable("patientNo") String patientNo,
                                                            @RequestBody
                                                            @Valid final VitalRecordData vital
    ) throws JSONException {

        Patient patient = patientService.findPatientOrThrow(patientNo);

        VitalsRecord vitalR = this.triageService.addVitalRecordsByPatient(patient, vital);
        Visit activeVisit = vitalR.getVisit();
        //log queue
        PatientQueue patientQueue = new PatientQueue();

        patientQueue.setPatient(patient);
        patientQueue.setVisit(activeVisit);

        if (activeVisit.getServiceType().equals(VisitEnum.ServiceType.Consultation) || activeVisit.getServiceType().equals(VisitEnum.ServiceType.Review)) {
            ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Consultation);
            patientQueue.setServicePoint(servicePoint);
            activeVisit.setServicePoint(servicePoint);
        }

        if (activeVisit.getServiceType().equals(VisitEnum.ServiceType.Consultation) || activeVisit.getServiceType().equals(VisitEnum.ServiceType.Review)) {
            activeVisit.setIsActiveOnConsultation(Boolean.TRUE);
//            if (activeVisit.getHealthProvider() == null && vital.getSendTo().equals("Service Point")) {
//                throw APIException.badRequest("Please specify the doctor", "");
//            }
//            if (activeVisit.getHealthProvider() == null && vital.getSendTo().equals("")) {
//                throw APIException.badRequest("Please specify the doctor", "");
//            }
        }

        if (vital.getSendTo().equals("specialist")) {
            Employee newDoctorSelected = employeeService.fetchEmployeeByNumberOrThrow(vital.getStaffNumber());
            updateVisitDoctor(activeVisit, newDoctorSelected, "Triage", activeVisit.getClinic().getId());

            patientQueue.setSpecialNotes("Sent from triage");

            if (activeVisit.getServiceType().equals(VisitEnum.ServiceType.Consultation) || activeVisit.getServiceType().equals(VisitEnum.ServiceType.Review)) {
                ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Consultation);
                patientQueue.setServicePoint(servicePoint);
                activeVisit.setServicePoint(servicePoint);
            } else {
                patientQueue.setStaffNumber(newDoctorSelected);
                patientQueue.setServicePoint(newDoctorSelected.getDepartment().getServicePointType());
            }

            activeVisit.setHealthProvider(newDoctorSelected);
//            if (activeVisit.getServiceType().equals(VisitEnum.ServiceType.Consultation) || activeVisit.getServiceType().equals(VisitEnum.ServiceType.Review)) {
//                activeVisit.setIsActiveOnConsultation(Boolean.TRUE);
//            }
        } else if (vital.getSendTo().equals("Service Point")) {
            ServicePoint servicePoint = servicePointService.getServicePoint(vital.getServicePointIdentifier());
            if (servicePoint.getServicePointType().equals(ServicePointType.Triage)) {
                throw APIException.conflict("Please select another service point. Patient is already on {0}", ServicePointType.Triage.name());
            }
            patientQueue.setServicePoint(servicePoint);
            patientQueue.setSpecialNotes("Sent from triage");
            activeVisit.setServicePoint(servicePoint);
        } else {
            //patientQueue.setStatus(false);
        }
        activeVisit.setTriageCategory(vital.getUrgency());
        //update visit details
        visitService.createAVisit(activeVisit);
        patientQueueService.createPatientQueue(patientQueue);
//        VitalRecordData vr = modelMapper.map(activeVisit, VitalRecordData.class);
        VitalRecordData vr = VitalRecordData.map(vitalR); // triageService.convertToVitalsData(vitalR);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/patient/{patientNo}/vitals/{id}")
                .buildAndExpand(patientNo, vitalR.getId()).toUri();
        auditTrailService.saveAuditTrail("Visit", "Entered Patient vitals for patient " + patient.getFullName());
        return ResponseEntity.created(location).body(vr);
    }
//
//    @GetMapping("/visits/{visitNumber}/vitals")
//    @PreAuthorize("hasAuthority('view_visits')")
//    @ApiOperation(value = "Fetch all patient vitals by visits", response = VitalRecordData.class)
//    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByVisit(@PathVariable("visitNumber")
//            final String visitNumber,
//            @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
//            Pageable pageable
//    ) {
//        Page<VitalRecordData> page = triageService.fetchVitalRecordsByVisit(visitNumber, pageable).map(v -> convertToVitalsData(v));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

//    @GetMapping("/patients/{patientNumber}/vitals")
//    @PreAuthorize("hasAuthority('view_visits')")
//    @ApiOperation(value = "Fetch all patient vitals by patient", response = VitalRecordData.class)
//    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByPatient(@PathVariable("patientNumber")
//            final String patientNumber,
//            @RequestParam(required = false) MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
//            Pageable pageable
//    ) {
//
//        Page<VitalRecordData> page = triageService.fetchVitalRecordsByPatient(patientNumber, pageable).map(v -> convertToVitalsData(v));
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }

//    @GetMapping("/patients/{patientNumber}/vitals/last")
//    @PreAuthorize("hasAuthority('view_visits')")
//    @ApiOperation(value = "Fetch all patient's last vitals by patient", response = VitalRecordData.class)
//    public ResponseEntity<?> fetchLatestVitalsByPatient(@PathVariable("patientNumber")
//            final String patientNumber
//    ) {
//
//        Optional<VitalsRecord> vr = triageService.fetchLastVitalRecordsByPatient(patientNumber);
//        if (vr.isPresent()) {
//            return ResponseEntity.ok(VitalRecordData.map(vr.get()));
//        } else {
//            return ResponseEntity.ok(new VitalRecordData());
//        }
//
//    }

    @GetMapping("/visits/list")
    @PreAuthorize("hasAuthority('view_visits')")
    public ResponseEntity<Pager<SimpleVisit>> getVisitList(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "dateRange", required = false) final String dateRange,
            @RequestParam(value = "page", required = false) final Integer pageNo,
            @RequestParam(value = "pageSize", required = false) final Integer pageSize
    ) {
        Pageable pageable = PaginationUtil.createPage(pageNo, pageSize);
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<SimpleVisit> page = visitService.getSimpleVisits(visitNumber, patientNumber, range, pageable)
                .map(SimpleVisit::map);
        auditTrailService.saveAuditTrail("Visit", "Viewed all Patient visits");
        return ResponseEntity.ok((Pager<SimpleVisit>) PaginationUtil.toPager(page, "Patient Visits"));
    }

    private VisitData convertToVisitData(Visit visit) {
        VisitData visitData = VisitData.map(visit);
        if (visit.getHealthProvider() != null) {
            visitData.setPractitionerCode(visit.getHealthProvider().getStaffNumber());
            visitData.setPractitionerName(visit.getHealthProvider().getTitle() + ". " + visit.getHealthProvider().getFullName());
        }
        if (visit.getServicePoint() != null) {
            visitData.setLocationIdentity(visit.getServicePoint().getId());
            visitData.setServicePointName(visit.getServicePoint().getName());
        }
        //fetch patient log
        List<PatientQueue> patientQueue = patientQueueService.fetchQueueByVisit(visit);
        if (!patientQueue.isEmpty()) {
            List<PatientQueueData> queueData = new ArrayList<>();
            patientQueue.stream().map((d) -> patientQueueService.convertToPatientQueueData(d)).forEachOrdered((data) -> {
                queueData.add(data);
            });
            visitData.setPatientQueueData(queueData);
        }
        if (visit.getClinic() != null) {
            visitData.setClinic(visit.getClinic().getClinicName());
        }
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        visitData.setPatientData(patientService.convertToPatientData(patient));
        return visitData;
    }

    private VitalRecordData convertToVitalsData(VitalsRecord vitalsRecord) {
        return modelMapper.map(vitalsRecord, VitalRecordData.class);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void updateVisitDoctor(Visit activeVisit, Employee newDoctorSelected, String reason, Long clinicId) {

        DoctorClinicItems newClinic = clinicService.fetchClinicById(clinicId);

        Optional<DoctorInvoice> currentDoctorInvoice = getCurrentDoctorInvoice(activeVisit);

        // we reverse the already invoiced doctor fee for the visit
        if (currentDoctorInvoice.isPresent()) {
            doctorInvoiceService.removeDoctorInvoice(currentDoctorInvoice.get());
        }
        //if clinic was changed cancel the bill and create new bill and invoice the doctor
        if (!Objects.equals(newClinic.getId(), activeVisit.getClinic().getId())) {

            if (currentDoctorInvoice.isPresent()) {
                billingService.cancelItem(currentDoctorInvoice.get().getBillItemId());
            }
            Optional<PaymentDetails> pd = paymentDetailsService.getPaymentDetailsByVist(activeVisit);
            Long schemeId = null;
            if (pd.isPresent()) {
                schemeId = pd.get().getScheme().getId();
            }

            createConsultationBill(activeVisit, clinicId, newDoctorSelected, schemeId);
        } else {
            //otherwise we just create a new doctors invoice using the existing bill
            Optional<DoctorItem> newChargeableDoctorItem = doctorInvoiceService.getDoctorItem(newDoctorSelected, activeVisit.getClinic().getServiceType());
            if (newChargeableDoctorItem.isPresent()) {
                doctorInvoiceService.createDoctorInvoice(activeVisit, newDoctorSelected, newChargeableDoctorItem.get());
            }
        }

        //log the audit for the changes
        SpecialistChangeAuditData data = new SpecialistChangeAuditData();
        data.setComments(reason);
        data.setDate(LocalDateTime.now());
        data.setVisitNumber(activeVisit.getVisitNumber());
        data.setToDoctor(newDoctorSelected.getFullName());
        data.setFromDoctor(activeVisit.getHealthProvider() != null ? activeVisit.getHealthProvider().getFullName() : "");
        specialistChangeAuditService.createSpecialistChangeAudit(data);
    }

    private Optional<DoctorInvoice> getCurrentDoctorInvoice(Visit visit) {
        Optional<DoctorItem> currentChargeDoctorItem = doctorInvoiceService.getDoctorItem(visit.getHealthProvider(), visit.getClinic().getServiceType());

        if (currentChargeDoctorItem.isPresent()) {
            return doctorInvoiceService.fetchDoctorInvoiceByVisitDoctorItemAndDoctor(visit, currentChargeDoctorItem.get(), visit.getHealthProvider());
        }
        return Optional.empty();
    }
}
