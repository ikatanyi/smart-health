package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.config.service.ConfigService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.laboratory.data.LabRegisterData;
import io.smarthealth.clinical.laboratory.data.LabRegisterTestData;
import io.smarthealth.clinical.laboratory.data.LabResultData;
import io.smarthealth.clinical.laboratory.data.PatientResults;
import io.smarthealth.clinical.laboratory.data.StatusRequest;
import io.smarthealth.clinical.laboratory.domain.LabRegister;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTest;
import io.smarthealth.clinical.laboratory.domain.LabResult;
import io.smarthealth.clinical.laboratory.domain.LabResultRepository;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.domain.LabTestRepository;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.laboratory.domain.specification.LabRegisterSpecification;
import io.smarthealth.clinical.laboratory.domain.specification.LabResultSpecification;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.clinical.laboratory.domain.LabRegisterRepository;
import io.smarthealth.clinical.laboratory.domain.LabRegisterTestRepository;
import io.smarthealth.clinical.laboratory.domain.specification.LabRegisterTestSpecification;
import io.smarthealth.clinical.radiology.domain.TotalTest;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.service.PaymentDetailsService;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.documents.data.DocResponse;
import io.smarthealth.documents.data.DocumentData;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.documents.service.FileStorageService;
import io.smarthealth.notification.data.NoticeType;
import io.smarthealth.notification.data.NotificationData;
import io.smarthealth.notification.service.NotificationEventPublisher;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.domain.WalkIn;
import io.smarthealth.organization.person.service.WalkingService;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.multipart.MultipartFile;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.Instant;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class LaboratoryService {

    private final LabRegisterRepository repository;
    private final LabTestRepository labTestRepository;
//    private final VisitRepository visitRepository;
    private final VisitService visitService;
    private final PaymentDetailsService paymentDetailsService;
    private final LabRegisterTestRepository testRepository;
    private final SequenceNumberService sequenceNumberService;
    private final LabResultRepository labResultRepository;
    private final WalkingService walkingService;
    private final ServicePointService servicePointService;
    private final BillingService billingService;
    private final DoctorsRequestRepository doctorRequestRepository;
    private final NotificationEventPublisher notificationEventPublisher;
    private final FileStorageService fileService;
    private final ConfigService configurationService;
    private final LabConfigurationService labConfigService;
    private final EmployeeService employeeService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LabRegister createLabRegister(LabRegisterData data) {

        LabRegister request = toLabRegister(data);

        String trnId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        String labNo = sequenceNumberService.next(1L, Sequences.LabNumber.name());
        request.setLabNumber(labNo);
        request.setTransactionId(trnId);
        data.setTransactionId(trnId);

        LabRegister savedRegister = repository.save(request);

//        billingService.save(toBill(data));
        billingService.save(toBill(savedRegister));
        //save
        data.getTests()
                .stream()
                .forEach(x -> {
                    fulfillDocRequest(x.getRequestId());
                });
        //notify registration has occured
        notificationEventPublisher.publishDocRequestEvent(Arrays.asList(DoctorRequestData.RequestType.Laboratory));

        return savedRegister;
    }

    public LabRegister getLabRegisterByNumber(String labNo) {
        return repository.findByLabNumber(labNo)
                .orElseThrow(() -> APIException.notFound("Lab Test with LabNumber {0} Not Found", labNo));
    }

    public LabRegister getLabRegisterById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Request with id {0} Not Found", id));
    }

    public LabRegister updateLabRegister(Long id, LabRegisterData data) {
        LabRegister requests = getLabRegisterById(id);

        return requests;//repository.save(requests);
    }

    @Transactional
    public int updateLabRegisteredTest(String labNo, Long testId, StatusRequest status) {
        LabRegister requests = getLabRegisterByNumber(labNo);

        LabRegisterTest test = requests.getTests()
                .stream()
                .filter(x -> Objects.equals(x.getId(), testId))
                .findAny()
                .orElseThrow(() -> APIException.notFound("Lab Test with Id {0} Not Found", testId));

        switch (status.getStatus()) {
            case Collected:
                repository.updateLabRegisterStatus(LabTestStatus.PendingResult, requests.getId());
                return testRepository.updateTestCollected(SecurityUtils.getCurrentUserLogin().orElse(""), status.getSpecimen(), testId, LabTestStatus.PendingResult);
            case Entered:
                repository.updateLabRegisterStatus(LabTestStatus.ResultsEntered, requests.getId());
                //results entered 
                updateRegisterStatus(requests);
                return testRepository.updateTestEntry(status.getDoneBy(), testId, LabTestStatus.ResultsEntered);
            case Validated:
                repository.updateLabRegisterStatus(LabTestStatus.Complete, requests.getId());
                return testRepository.updateTestValidation(status.getDoneBy(), testId, LabTestStatus.Complete);
            case Paid:
//                test.setPaid(Boolean.TRUE);
                return testRepository.updateTestPaid(testId);
            default:
                return 0;
        }
    }

    public void voidLabRegister(Long id) {
        LabRegister requests = getLabRegisterById(id);
        requests.setVoided(Boolean.TRUE);
        repository.save(requests);
    }

    @Transactional
    private void updateRegisterStatus(LabRegister requests) {

        if (requests.isCompleted()) {
            repository.updateLabRegisterStatus(LabTestStatus.Complete, requests.getId());
        } else {
            repository.updateLabRegisterStatus(LabTestStatus.PartialResult, requests.getId());
        }
    }

    @Transactional
    public void markResultRegisterStatusAsRead(LabRegisterTest registerTest) {
        registerTest.setResultRead(Boolean.TRUE);
        testRepository.save(registerTest);
    }

    public Page<LabRegister> getLabRegister(String labNumber, String orderNumber, String visitNumber, String patientNumber, List<LabTestStatus> status, DateRange range, String search, Pageable page) {
        Specification<LabRegister> spec = LabRegisterSpecification.createSpecification(labNumber, orderNumber, visitNumber, patientNumber, status, range, search);
        return repository.findAll(spec, page);
    }

    public Page<LabRegisterTest> getLabRegisterTest(String labNumber, String orderNumber, String visitNumber, String patientNumber, LabTestStatus status, DateRange range, String search, Pageable page) {
        Specification<LabRegisterTest> spec = LabRegisterTestSpecification.createSpecification(labNumber, orderNumber, visitNumber, patientNumber, status, range, search);
        return testRepository.findAll(spec, page);
    }

//RESULTS
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public LabResult createLabResult(LabResultData data) {
        LabResult result = toLabResult(data);
        LabResult savedResult = labResultRepository.save(result);
        updateResultsEntry(savedResult, null);
        //send notifications
        doNotifyUser(Arrays.asList(savedResult.getLabRegisterTest()));

        return savedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<LabResult> createLabResult(List<LabResultData> data) {

        List<LabResult> toSave = data
                .stream()
                .map(x -> toLabResult(x))
                .collect(Collectors.toList());

        List<LabResult> savedResults = labResultRepository.saveAll(toSave);
        ArrayList<LabRegisterTest> testToNotify = new ArrayList<>();

        savedResults.forEach(x -> updateResultsEntry(x, testToNotify));

        //if notification is set on
        doNotifyUser(testToNotify);

        return savedResults;
    }

    public LabResult getResult(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Result with Id  {0} Not Found", id));
    }

    public LabResult updateLabResult(Long id, LabResultData data) {
        LabResult result = getResult(id);
        return result;
    }

    public void voidLabResult(Long id) {
        LabResult result = getResult(id);
        result.setVoided(Boolean.TRUE);
        labResultRepository.save(result);
    }

    public List<LabResultData> getLabResultDataByVisit(Visit visit) {
        return getResultByVisit(visit)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());
    }

    public List<LabResult> getResultByVisit(Visit visit) {
        return labResultRepository.findByVisit(visit);
    }

    public List<LabResult> getResultByRegister(LabRegister register) {
        return labResultRepository.findByLabRegisterNumber(register);
    }

    public Page<LabResult> getLabResults(String visitNumber, String patientNumber, String labNumber, Boolean walkin, String testName, String orderNumber, DateRange range, Pageable page) {
        Specification<LabResult> spec = LabResultSpecification.createSpecification(visitNumber, patientNumber, labNumber, walkin, testName, orderNumber, range);
        return labResultRepository.findAll(spec, page);
    }

    @Transactional
    private void updateResultsEntry(LabResult savedResult, ArrayList<LabRegisterTest> testToNotify) {

        LabRegisterTest test = savedResult.getLabRegisterTest();

        if (testToNotify != null && !testToNotify.contains(test)) {
            testToNotify.add(test);
        }

        testRepository.updateTestEntry(SecurityUtils.getCurrentUserLogin().orElse("system"), test.getId(), LabTestStatus.ResultsEntered);

        updateRegisterStatus(test.getLabRegister());

    }

    private LabResult toLabResult(LabResultData data) {
        LabRegisterTest labRequestTest = getLabRegisterTest(data.getLabRegisterTestId());

        LabResult results = new LabResult();
        results.setAnalyte(data.getAnalyte());
        results.setLabNumber(data.getLabNumber());
        results.setLabRegisterTest(labRequestTest);
        results.setLowerLimit(data.getLowerLimit());
        results.setPatientNo(data.getPatientNo());
        results.setReferenceValue(data.getReferenceValue());
        results.setResultValue(data.getResultValue());
        results.setResultsDate(LocalDateTime.now());
        results.setUnits(data.getUnits());
        results.setUpperLimit(data.getUpperLimit());
        results.setComments(data.getComments());
        results.setStatus(data.getStatus());
        results.setEnteredBy(data.getEnteredBy());
        results.setValidatedBy(data.getValidatedBy());
        results.setResultRead(Boolean.FALSE);
        return results;
    }

    private LabRegister toLabRegister(LabRegisterData data) {
        LabRegister request = new LabRegister();
        request.setOrderNumber(data.getOrderNumber());
        request.setIsWalkin(data.getIsWalkin());
        request.setPaymentMode(data.getPaymentMode());
        if (!data.getIsWalkin()) {
            Visit visit = getPatientVisit(data.getVisitNumber());
            request.setVisit(visit);
            request.setRequestedBy(data.getRequestedBy());

            if (data.getRequestedBy() == null && data.getMedicId() != null) {
                Optional<Employee> employee = employeeService.findByEmployeeID(data.getMedicId());
                if (employee.isPresent()) {
                    request.setRequestedBy(employee.get().getFullName());
                }
            }

            request.setPatientNo(visit.getPatient().getPatientNumber());

            Optional<PaymentDetails> visitPayDetails = paymentDetailsService.getPaymentDetailsByVist(visit);
            if (visitPayDetails.isPresent()) {
                PaymentDetails pd = visitPayDetails.get();
                if (pd.getLimitReached()) {
                    request.setPaymentMode("Cash");
                }
            }

        } else {
            WalkIn w = createWalking(data.getPatientName());
            if (data.getMedicId() != null) {
                Optional<Employee> employee = employeeService.findByEmployeeID(data.getMedicId());
                if (employee.isPresent()) {
                    request.setRequestedBy(employee.get().getFullName());
                }
            }
            request.setPatientNo(w.getWalkingIdentitificationNo());
            request.setPaymentMode("Cash");
        }
        String method = request.getPaymentMode();
        System.out.println("method " + method);

        request.setRequestDatetime(data.getRequestDatetime());
        request.setStatus(LabTestStatus.AwaitingSpecimen);
        ArrayList<LabRegisterTestData> panels = new ArrayList<>();

        List<LabRegisterTest> registeredlist = data.getTests()
                .stream()
                .map(x -> toLabRegisterTest(x, method, panels))
                .filter(x -> x != null)
                .collect(Collectors.toList());

        if (!panels.isEmpty()) {
            panels.forEach(x -> {
                registeredlist.addAll(registerPanelTests(x, method));
            });
        }
        request.addPatientTest(registeredlist);

        return request;
    }

    private LabRegisterTest toLabRegisterTest(LabRegisterTestData data, String paymentMode, ArrayList<LabRegisterTestData> panels) {
        LabTest labTest = getLabTest(data.getTestId());
        if (labTest.getIsPanel() != null && labTest.getIsPanel()) {
            panels.add(data);

            return null;
        }

        LabRegisterTest test = new LabRegisterTest();
        test.setCollected(Boolean.FALSE);
        test.setEntered(Boolean.FALSE);
        test.setLabTest(labTest);
        test.setPaymentMethod(data.getPaymentMethod());
        test.setPaid(paymentMode.equals("Cash") ? Boolean.FALSE : Boolean.TRUE);
        test.setVoided(Boolean.FALSE);
        test.setValidated(Boolean.FALSE);
        test.setRequestId(data.getRequestId());
        test.setPrice(data.getTestPrice());
        test.setStatus(data.getStatus());
        test.setIsPanel(labTest.getIsPanel());
        test.setReferenceNo(data.getReferenceNo());

        test.setStatus(LabTestStatus.AwaitingSpecimen);
        return test;
    }

    private List<LabRegisterTest> registerPanelTests(LabRegisterTestData data, String paymentMode) {
        LabTest labTest = getLabTest(data.getTestId());

        if (labTest.getIsPanel()) {
            return labTest.getPanelTests()
                    .stream()
                    .map(x -> {
                        LabRegisterTest test = new LabRegisterTest();
                        test.setCollected(Boolean.FALSE);
                        test.setEntered(Boolean.FALSE);
                        test.setLabTest(x);
//                        test.setPaid(paymentMode.equals("Cash") ? Boolean.FALSE : Boolean.TRUE);
                        if (paymentMode.equals("Cash")) {
                            Optional<Visit> visit = visitService.findVisit(data.getVisitNumber());
                            if (visit.isPresent() && visit.get().getVisitType() == VisitEnum.VisitType.Inpatient) {
                                test.setPaid(Boolean.TRUE);
                            } else {
                                test.setPaid(Boolean.FALSE);
                            }
                        } else {
                            test.setPaid(Boolean.TRUE);
                        }

                        test.setPaid(Boolean.TRUE);
                        test.setVoided(Boolean.FALSE);
                        test.setValidated(Boolean.FALSE);
                        test.setRequestId(data.getRequestId());
                        test.setPrice(data.getTestPrice());
                        test.setStatus(data.getStatus());
                        test.setIsPanel(Boolean.TRUE);
                        test.setReferenceNo(data.getReferenceNo());
                        test.setParentLabTest(labTest);

                        test.setStatus(LabTestStatus.AwaitingSpecimen);
                        test.setPaymentMethod(data.getPaymentMethod());
                        return test;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    private LabTest getLabTest(Long id) {
//        return labTestRepository.findById(id)
        return labTestRepository.findByItemId(id)
                .orElseThrow(() -> APIException.notFound("Lab Test with Id {0} Not Found", id));
    }

    private Visit getPatientVisit(String visitNo) {
        return visitService.findVisitEntityOrThrow(visitNo);
    }

    public LabRegisterTest getLabRegisterTest(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Lab Request Test with Id {0} Not Found", id));
    }

    public PatientResults getPatientResults(String patientNo, String visitNumber) {
        Specification<LabRegister> spec = LabRegisterSpecification.createSpecification(null, null, visitNumber, null, null, null, null);
        List<LabRegister> lists = repository.findAll(spec);
        PatientResults results = new PatientResults();

        return results;
    }

    public List<LabRegisterTest> getTestsResultsByVisit(String visitNo, String labNumber) {
        if (labNumber != null && !labNumber.equals("")) {
            return testRepository.findTestsByVisitAndLabNo(visitNo, labNumber);
        }
        return testRepository.findTestsByVisitNumber(visitNo);
    }

    public List<LabRegisterTest> getLabTests(LabTest test) {
        return testRepository.findByLabTest(test);
    }

    public List<LabRegisterTest> getLabTestsByDate(LabTest test, LocalDateTime date1, LocalDateTime date2) {
        return testRepository.findByLabTestAndEntryDateTimeBetween(test, date1, date2);
    }

    public List<LabRegisterTest> getTestsByDate(DateRange range) {
        return testRepository.findTestsByDateRange(range.getStartDateTime(), range.getEndDateTime());
    }

    public List<TotalTest> getPatientTestTotals(Instant fromDate, Instant toDate) {
        return testRepository.findTotalTests(fromDate, toDate);
    }

    private WalkIn createWalking(String patientName) {
        WalkIn w = new WalkIn();
        w.setFirstName(patientName);
        w.setSurname("WI");
        return walkingService.createWalking(w);
    }

    private PatientBill toBill(/*LabRegisterData*/LabRegister data) {

        //get the service point from store
        Visit visit = data.getVisit();// visitRepository.findByVisitNumber(data.getVisitNumber()).orElse(null);
        //find the service point for lab
        ServicePoint srvpoint = servicePointService.getServicePointByType(ServicePointType.Laboratory);

        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(visit);
        if (visit != null) {
            patientbill.setPatient(visit.getPatient());
            patientbill.setWalkinFlag(Boolean.FALSE);
        }
        // how do I deal with bills for Walkin
        if (data.getIsWalkin()) {
            Optional<WalkIn> wi = walkingService.fetchWalkingByWalkingNo(data.getPatientNo());
            if (wi.isPresent()) {
                patientbill.setOtherDetails(wi.get().getFirstName()+" "+wi.get().getSurname());
            } else {
                patientbill.setOtherDetails(data.get);
            }
            patientbill.setReference(data.getPatientNo());
//            patientbill.setOtherDetails(data.getRequestedBy());
            patientbill.setWalkinFlag(Boolean.TRUE);
        }
        String method = data.getPaymentMode() != null ? data.getPaymentMode() : "Cash";
        patientbill.setBillingDate(LocalDate.now());
//        patientbill.setReferenceNo(data.getReferenceNo());
        patientbill.setPaymentMode(method);
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);
        ArrayList<LabTest> parentLabTest = new ArrayList<>();
        List<PatientBillItem> lineItems = data.getTests()
                .stream()
                .map(lineData -> toPatientBill(lineData, srvpoint, data.getTransactionId(), method, parentLabTest)
                //                {
                //                    PatientBillItem billItem = new PatientBillItem();
                ////                    Item item = billingService.getItemByBy(lineData.getTestId());
                //                    Item item = lineData.getLabTest().getService();
                //
                //                    billItem.setBillingDate(LocalDate.now());
                //                    billItem.setTransactionId(data.getTransactionId());
                //                    billItem.setServicePointId(srvpoint.getId());
                //                    billItem.setServicePoint(srvpoint.getName());
                //                    billItem.setItem(item);
                ////                    billItem.setPrice(lineData.getTestPrice().doubleValue());
                //                    billItem.setPrice(lineData.getPrice().doubleValue());
                //                    billItem.setQuantity(1d);
                ////                    billItem.setAmount(lineData.getTestPrice().doubleValue());
                //                    billItem.setAmount(lineData.getPrice().doubleValue());
                //                    billItem.setDiscount(0.00);
                //                    billItem.setPaid(data.getPaymentMode() != null ? data.getPaymentMode().equals("Insurance") : false);
                ////                    billItem.setBalance(lineData.getTestPrice().doubleValue());
                //                    billItem.setBalance(lineData.getPrice().doubleValue());
                //                    billItem.setServicePoint(srvpoint.getName());
                //                    billItem.setServicePointId(srvpoint.getId());
                //                    billItem.setStatus(BillStatus.Draft);
                //                    billItem.setRequestReference(lineData.getId());
                //                    return billItem;
                //                }
                )
                .filter(z -> z != null)
                .collect(Collectors.toList());
        // now that we have the panel, we find out there services
        parentLabTest.forEach(x -> {
            PatientBillItem billItem = new PatientBillItem();
            Item item = x.getService();

            billItem.setBillingDate(LocalDate.now());
            billItem.setTransactionId(data.getTransactionId());
            billItem.setServicePointId(srvpoint.getId());
            billItem.setServicePoint(srvpoint.getName());
            billItem.setItem(item);
            Double price = x.getPanelPrice().doubleValue();
            billItem.setPrice(price);
            billItem.setQuantity(1d);
            billItem.setAmount(price);
            billItem.setDiscount(0.00);
            billItem.setPaid(data.getPaymentMode() != null ? data.getPaymentMode().equals("Insurance") : false);
            billItem.setBalance(price);
            billItem.setServicePoint(srvpoint.getName());
            billItem.setServicePointId(srvpoint.getId());
            billItem.setStatus(BillStatus.Draft);
            billItem.setBillPayMode(x.getPaymentMethod());
            //TODO enter the bills as array 
//            billItem.setRequestReference(); //this is the one I need to know how to handle ot
            lineItems.add(billItem);

        });
        Double amount = patientbill.getBillTotals();
        patientbill.setAmount(amount);
        patientbill.setDiscount(0D);
        patientbill.setBalance(amount);
        patientbill.addBillItems(lineItems);
        return patientbill;
    }

    private PatientBillItem toPatientBill(LabRegisterTest registeredTest, ServicePoint srvpoint, String transId, String paymentMethod, List<LabTest> panels) {
        if (registeredTest.getIsPanel() != null && registeredTest.getIsPanel()) {
            if (registeredTest.getParentLabTest() != null && !panels.contains(registeredTest.getParentLabTest())) {
                LabTest panelTest = registeredTest.getParentLabTest();
                panelTest.setPaymentMethod(registeredTest.getPaymentMethod());
                panelTest.setPanelPrice(registeredTest.getPrice());
                panels.add(panelTest);
            }

            return null;
        }

        PatientBillItem billItem = new PatientBillItem();
        Item item = registeredTest.getLabTest().getService();

        billItem.setBillingDate(LocalDate.now());
        billItem.setTransactionId(transId);
        billItem.setServicePointId(srvpoint.getId());
        billItem.setServicePoint(srvpoint.getName());
        billItem.setItem(item);
        billItem.setPrice(registeredTest.getPrice().doubleValue());
        billItem.setQuantity(1d);
        billItem.setAmount(registeredTest.getPrice().doubleValue());
        billItem.setDiscount(0.00);
        billItem.setBillPayMode(registeredTest.getPaymentMethod());
        //check limit amount not the global payment method during visit activation
        Optional<PaymentDetails> visitPayDetails = paymentDetailsService.getPaymentDetailsByVist(registeredTest.getLabRegister().getVisit());
        if (visitPayDetails.isPresent()) {
            PaymentDetails pd = visitPayDetails.get();
            if (pd.getLimitReached()) {
                billItem.setPaid(false);
            } else {
                billItem.setPaid(paymentMethod != null ? paymentMethod.equals("Insurance") : false);
            }
        } else {
            billItem.setPaid(paymentMethod != null ? paymentMethod.equals("Insurance") : false);
        }
        billItem.setBalance(registeredTest.getPrice().doubleValue());
        billItem.setServicePoint(srvpoint.getName());
        billItem.setServicePointId(srvpoint.getId());
        billItem.setStatus(BillStatus.Draft);
        billItem.setRequestReference(registeredTest.getId());
        return billItem;
    }

    private void fulfillDocRequest(Long id) {
        if (id == null) {
            return;
        }
        DoctorRequest req = doctorRequestRepository.findById(id).orElse(null);
        if (req != null) {
            req.setFulfillerStatus(FullFillerStatusType.Fulfilled);
            doctorRequestRepository.save(req);
        }
    }

    public LabTestStatus LabTestStatusToEnum(String status) {
        if (status == null || status.equals("null") || status.equals("")) {
            return null;
        }
        if (EnumUtils.isValidEnum(LabTestStatus.class, status)) {
            return LabTestStatus.valueOf(status);
        }
        throw APIException.internalError("Provide a Valid Invoice Status");
    }

    @Transactional
    public DocResponse uploadDocument(Long testId, String name, MultipartFile file) {
//        LabRegisterTest labRequestTest = getLabRegisterTest(testId);
        ServicePoint srvpoint = servicePointService.getServicePointByType(ServicePointType.Laboratory);

        DocumentData data = new DocumentData();
        data.setDocfile(file);
        data.setDocumentNumber("" + testId);
        data.setDocumentType(DocumentType.LabReport);
        data.setFileName(name);
        data.setServicePointId(srvpoint.getId());
        DocResponse doc = fileService.documentUpload(data).toSimpleData();

        testRepository.addAttachment(doc.getDocumentName(), testId);

        return doc;
    }

    private void doNotifyUser(List<LabRegisterTest> testToNotify) {

        //TODO
        testToNotify.stream()
                .forEach(x -> {
                    if (x.getLabRegister().getVisit() != null && x.getRequestId() != null) {
                        Visit visit = x.getLabRegister().getVisit();
                        String description = String.format("Patient %s %s - %s Results are Ready", visit.getPatient().getPatientNumber(), visit.getPatient().getFullName(), x.getLabTest().getTestName());
                        String reference = String.valueOf(x.getId());

                        DoctorRequest req = doctorRequestRepository.findById(x.getRequestId()).orElse(null);
                        if (req != null) {
                            //
                            if (req.getRequestedBy() != null) {
                                notificationEventPublisher.publishUserNotificationEvent(NotificationData.builder()
                                        .datetime(LocalDateTime.now())
                                        .description(description)
                                        .isRead(false)
                                        .noticeType(NoticeType.LaboratoryResults)
                                        .username(req.getRequestedBy().getUsername())
                                        .reference(reference)
                                        .build()
                                );
                            }
                        }
                    }
                });
    }
}
