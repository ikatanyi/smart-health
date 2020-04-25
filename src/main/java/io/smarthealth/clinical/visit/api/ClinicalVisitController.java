package io.smarthealth.clinical.visit.api;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import io.smarthealth.accounting.doctors.service.DoctorClinicService;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.domain.PriceListRepository;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.queue.domain.PatientQueue;
import io.smarthealth.clinical.queue.service.PatientQueueService;
import io.smarthealth.clinical.record.data.TriageNotesData;
import io.smarthealth.clinical.record.data.VitalRecordData;
import io.smarthealth.clinical.record.domain.TriageNotes;
import io.smarthealth.clinical.record.domain.VitalsRecord;
import io.smarthealth.clinical.record.service.TriageNotesService;
import io.smarthealth.clinical.record.service.TriageService;
import io.smarthealth.clinical.visit.data.PaymentDetailsData;
import io.smarthealth.clinical.visit.data.VisitData;
import io.smarthealth.clinical.visit.data.enums.TriageCategory;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.service.SchemeService;
import io.smarthealth.infrastructure.common.ApiResponse;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.PageDetails;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
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

    @PostMapping("/visits")
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
        if (visitData.getPaymentMethod().equals(VisitEnum.PaymentMethod.Insurance)) {
            System.out.println("Payment method is insurance ");
            Scheme scheme = schemeService.fetchSchemeById(visitData.getPayment().getSchemeId());
            Optional<SchemeConfigurations> config = schemeService.fetchSchemeConfigByScheme(scheme);
            PaymentDetails pd = PaymentDetailsData.map(visitData.getPayment());

            pd.setScheme(scheme);
            pd.setPayer(scheme.getPayer());
            pd.setVisit(visit);
            if (config.isPresent()) {
                pd.setCoPayCalcMethod(config.get().getCoPayType());
                pd.setCoPayValue(config.get().getCoPayValue());
            }
            paymentDetailsService.createPaymentDetails(pd);
            //create bill for copay

            if (config.isPresent()) {
                System.out.println("Config data found");
                if (config.get().getCoPayType().equals(CoPayType.Fixed)) {
                    System.out.println("Copay type is fixed");
                    Pageable firstPageWithOneElement = PageRequest.of(0, 1);

                    //find item where item category is copay
                    Page<Item> item = itemService.findByItemsByCategory(ItemCategory.CoPay, firstPageWithOneElement);
                    if (item.getContent().size() < 1) {
                        throw APIException.notFound("Copay service item has not been set", "CoPay");
                    }
                    List<BillItemData> billItems = new ArrayList<>();
                    BillItemData itemData = new BillItemData();
                    itemData.setAmount(config.get().getCoPayValue());
                    itemData.setBalance(config.get().getCoPayValue());
                    itemData.setBillingDate(LocalDate.now());
                    itemData.setItem(item.getContent().get(0).getItemName());
                    itemData.setItemCode(item.getContent().get(0).getItemCode());

                    itemData.setQuantity(1.0);
                    itemData.setServicePoint(visit.getServicePoint().getName());
                    itemData.setServicePointId(visit.getServicePoint().getId());
                    billItems.add(itemData);

                    BillData data = new BillData();
                    data.setWalkinFlag(false);
                    data.setBillItems(billItems);
                    data.setAmount(config.get().getCoPayValue());
                    data.setBalance(config.get().getCoPayValue());
                    data.setBillingDate(LocalDate.now());
                    data.setDiscount(0.00);
                    data.setPatientName(patient.getFullName());
                    data.setPatientNumber(patient.getPatientNumber());
                    data.setPaymentMode(visit.getPaymentMethod().name());
                    data.setVisitNumber(visit.getVisitNumber());
                    System.out.println("About to bill copay " + config.get().getCoPayValue());
                    billingService.createPatientBill(data);
                    System.out.println("End of creating copay bill " + data.toString());
                }
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
        if (visit.getServiceType().equals(VisitEnum.ServiceType.Consultation)) {
            DoctorClinicItems clinic = clinicService.fetchClinicById(visitData.getItemToBill());
            //PriceList pricelist = pricelistService.fetchPriceListByItemAndPriceBook(clinic.getServiceType(), null);
            //TODO: use pricelist not item service i.e fetchPriceListByItemAndPriceBook
            Item item = clinic.getServiceType();
            List<BillItemData> billItems = new ArrayList<>();
            BillItemData itemData = new BillItemData();
            itemData.setAmount(item.getRate().doubleValue());
            itemData.setBalance(item.getRate().doubleValue());
            itemData.setBillingDate(LocalDate.now());
            itemData.setPrice(item.getRate().doubleValue());
            itemData.setItem(item.getItemName());
            itemData.setItemCode(item.getItemCode());
            if (employee != null) {
                itemData.setMedicId(employee.getId());
                itemData.setMedicName(employee.getFullName());
            }
            itemData.setQuantity(1.0);
            itemData.setServicePoint(visit.getServicePoint().getName());
            itemData.setServicePointId(visit.getServicePoint().getId());
            billItems.add(itemData);

            BillData data = new BillData();
            data.setWalkinFlag(false);
            data.setBillItems(billItems);
            data.setAmount(item.getRate().doubleValue());
            data.setBalance(item.getRate().doubleValue());
            data.setBillingDate(LocalDate.now());
            data.setDiscount(0.00);
            data.setPatientName(patient.getFullName());
            data.setPatientNumber(patient.getPatientNumber());
            data.setPaymentMode(visit.getPaymentMethod().name());
            data.setVisitNumber(visit.getVisitNumber());

            billingService.createPatientBill(data);
        }
        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}")
                .buildAndExpand(visit.getVisitNumber()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.successMessage("Visit was activated successfully", HttpStatus.CREATED, visitDat));
    }

    @PutMapping("/visits/{visitNumber}")
    @ApiOperation(value = "Update patient visit record", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitRecord(@PathVariable("visitNumber") final String visitNumber, @RequestBody @Valid final VisitData visitData) {
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

        return ResponseEntity.created(location).body(visitDat);
    }

    @PutMapping("/visits/{visitNumber}/status/{status}")
    @ApiOperation(value = "Update patient visit status", response = VisitData.class)
    public @ResponseBody
    ResponseEntity<?> updateVisitStatus(@PathVariable("visitNumber") final String visitNumber, @PathVariable("status") final String status) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);

        visit.setStatus(VisitEnum.Status.valueOf(status));
        visit = this.visitService.createAVisit(visit);
        //Convert to data
        VisitData visitDat = VisitData.map(visit);

        return ResponseEntity.status(HttpStatus.OK).body(visitDat);
    }

    @GetMapping("/visits")
    public ResponseEntity<List<VisitData>> fetchAllVisits(
            @RequestParam(value = "visitNumber", required = false) final String visitNumber,
            @RequestParam(value = "staffNumber", required = false) final String staffNumber,
            @RequestParam(value = "servicePointType", required = false) final String servicePointType,
            @RequestParam(value = "patientNumber", required = false) final String patientNumber,
            @RequestParam(value = "patientName", required = false) final String patientName,
            @RequestParam(value = "runningStatus", required = false, defaultValue = "true") final boolean runningStatus,
            @RequestParam(value = "startDate", required = false) final String dateRange,
            Pageable pageable) {
        DateRange range = DateRange.fromIsoStringOrReturnNull(dateRange);
        Page<VisitData> page = visitService.fetchAllVisits(visitNumber, staffNumber, servicePointType, patientNumber, patientName, runningStatus, range, pageable).map(v -> convertToVisitData(v));
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @GetMapping("/visits/{visitNo}/payment-mode")
    public ResponseEntity<?> fetchpaymentModeByVisit(@PathVariable("visitNo") String visitNo) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNo);
        PaymentDetails pde = paymentDetailsService.fetchPaymentDetailsByVisit(visit);
        Pager<PaymentDetailsData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Payment Mode");
        pagers.setContent(PaymentDetailsData.map(pde));

        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @GetMapping("/patients/{patientNumber}/active-visit")
    public ResponseEntity<?> fetchActiveVisitByPatient(@PathVariable("patientNumber") String patientNumber) {
        Patient patient = visitService.findPatientOrThrow(patientNumber);
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

    @GetMapping("/patients/{id}/visits")
    public ResponseEntity<List<VisitData>> fetchAllVisitsByPatient(@PathVariable("id") final String patientNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        System.out.println("patientNumber " + patientNumber);
        Page<VisitData> page = visitService.fetchVisitByPatientNumber(patientNumber, pageable).map(v -> convertToVisitData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping("/visits/{visitNumber}/vitals")
    @ApiOperation(value = "Create/Add a new patient vital by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByVisit(@PathVariable("visitNumber") String visitNumber, @RequestBody @Valid final VitalRecordData vital) {
        Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        VitalsRecord vitalR = this.triageService.addVitalRecordsByVisit(visit, vital);

        VitalRecordData vr = modelMapper.map(vitalR, VitalRecordData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/visits/{visitNumber}/vitals/{id}")
                .buildAndExpand(visitNumber, vitalR.getId()).toUri();

        return ResponseEntity.created(location).body(vr);
    }

    @PostMapping("/visits/{visitNumber}/triage-notes")
    @ApiOperation(value = "Create/Add a new patient triage notes by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> addTriageNotesByVisit(@PathVariable("visitNumber") String visitNumber, @RequestBody @Valid final TriageNotesData triageNotesData) {
        final Visit visit = visitService.findVisitEntityOrThrow(visitNumber);
        TriageNotes e = TriageNotesData.map(triageNotesData);
        e.setVisit(visit);

        Pager<TriageNotesData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Triage notes has successfully been saved");
        pagers.setContent(TriageNotesData.map(triageNotesService.createNewTriageNotes(e)));
        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);
    }

    @GetMapping("/visits/{visitNumber}/triage-notes")
    @ApiOperation(value = "Create/Add a new patient triage notes by visit number", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> fetchTriageNotesByVisit(@PathVariable("visitNumber") String visitNumber, final Pageable pageable) {
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
        return ResponseEntity.status(HttpStatus.OK)
                .body(pagers);
    }

    @GetMapping("/triage-notes/{id}")
    @ApiOperation(value = "Fetch triage notes by id", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<?> findTriageNotesById(@PathVariable("v") final Long id) {
        TriageNotes e = triageNotesService.fetchTriageNoteById(id);
        Pager<TriageNotesData> pagers = new Pager();
        pagers.setCode("0");
        pagers.setMessage("Triage notes");
        pagers.setContent(TriageNotesData.map(e));
        return ResponseEntity.status(HttpStatus.OK).body(pagers);
    }

    @PostMapping("/patient/{patientNo}/vitals")
    //@ApiOperation(value = "", response = VitalRecordData.class)
    public @ResponseBody
    ResponseEntity<VitalRecordData> addVitalRecordByPatient(@PathVariable("patientNo") String patientNo, @RequestBody @Valid final VitalRecordData vital) {
        System.out.println("patientNo ");
        System.out.println("patientNo " + patientNo);
        Patient patient = patientService.findPatientOrThrow(patientNo);

        VitalsRecord vitalR = this.triageService.addVitalRecordsByPatient(patient, vital);
        Visit activeVisit = vitalR.getVisit();
        //log queue
        PatientQueue patientQueue = new PatientQueue(); //patientQueueService.fetchQueueByVisitNumber(vitalR.getVisit());
        //patientQueue.setUrgency(TriageCategory.valueOf(vital.getUrgency()));
        patientQueue.setPatient(patient);
        patientQueue.setVisit(activeVisit);
        if (vital.getSendTo().equals("specialist")) {
            Employee employee = employeeService.fetchEmployeeByNumberOrThrow(vital.getStaffNumber());
            //check if visit already has a doctor
            if (activeVisit.getHealthProvider() != null) {
                //update bill with current doctor if there is a difference between the visit activated one and the new one
                if (!employee.equals(activeVisit.getHealthProvider())) {
                    //find doctor invoice with service item and visit
                    //TODO: to complete this logic
                }
            }

            patientQueue.setStaffNumber(employee);
            patientQueue.setServicePoint(employee.getDepartment().getServicePointType());
            patientQueue.setSpecialNotes("Sent from triage");
            activeVisit.setServicePoint(employee.getDepartment().getServicePointType());
            activeVisit.setHealthProvider(employee);
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
        activeVisit.setTriageCategory(TriageCategory.valueOf(vital.getUrgency()));
        //update visit details
        visitService.createAVisit(activeVisit);
        patientQueueService.createPatientQueue(patientQueue);
        VitalRecordData vr = modelMapper.map(activeVisit, VitalRecordData.class);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/patient/{patientNo}/vitals/{id}")
                .buildAndExpand(patientNo, vitalR.getId()).toUri();

        return ResponseEntity.created(location).body(vr);
    }

    @GetMapping("/visits/{visitNumber}/vitals")
    @ApiOperation(value = "Fetch all patient vitals by visits", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByVisit(@PathVariable("visitNumber") final String visitNumber, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<VitalRecordData> page = triageService.fetchVitalRecordsByVisit(visitNumber, pageable).map(v -> convertToVitalsData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{patientNumber}/vitals")
    @ApiOperation(value = "Fetch all patient vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<List<VitalRecordData>> fetchAllVitalsByPatient(@PathVariable("patientNumber") final String patientNumber, @RequestParam(required = false) MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {

        Page<VitalRecordData> page = triageService.fetchVitalRecordsByPatient(patientNumber, pageable).map(v -> convertToVitalsData(v));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/patients/{patientNumber}/vitals/last")
    @ApiOperation(value = "Fetch all patient's last vitals by patient", response = VitalRecordData.class)
    public ResponseEntity<?> fetchLatestVitalsByPatient(@PathVariable("patientNumber") final String patientNumber) {

        Optional<VitalsRecord> vr = triageService.fetchLastVitalRecordsByPatient(patientNumber);
        if (vr.isPresent()) {
            return ResponseEntity.ok(VitalRecordData.map(vr.get()));
        } else {
            return ResponseEntity.ok(new VitalRecordData());
        }

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
        Patient patient = patientService.findPatientOrThrow(visitData.getPatientNumber());
        visitData.setPatientData(patientService.convertToPatientData(patient));
        return visitData;
    }

    private VitalRecordData convertToVitalsData(VitalsRecord vitalsRecord) {
        return modelMapper.map(vitalsRecord, VitalRecordData.class);
    }

}
