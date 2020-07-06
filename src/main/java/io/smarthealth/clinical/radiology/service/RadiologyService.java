package io.smarthealth.clinical.radiology.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.data.RadiologyResultData;
import io.smarthealth.clinical.radiology.data.ScanItemData;
import io.smarthealth.clinical.radiology.domain.PatientRadiologyTestRepository;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.PatientScanTestRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.RadiologyResultRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyRegisterSpecification;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyResultSpecification;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyTestSpecification;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.documents.data.DocumentData;
import io.smarthealth.documents.domain.Document;
import io.smarthealth.documents.domain.enumeration.DocumentType;
import io.smarthealth.documents.service.FileStorageService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.notifications.service.RequestEventPublisher;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class RadiologyService {

    private final PatientRadiologyTestRepository patientradiologyRepository;

    private final DoctorsRequestRepository doctorRequestRepository;

    private final PatientScanTestRepository pscanRepository;

    private final BillingService billService;

    private final ServicePointService servicePointService;

    private final EmployeeService employeeService;

    private final ItemService itemService;

    private final VisitService visitService;

    private final SequenceNumberService sequenceNumberService;

    private final RadiologyConfigService radiologyConfigService;

    private final RadiologyResultRepository radiologyResultRepo;

    private final PatientScanTestRepository registerTestRepository;

    private final FileStorageService fileStoerageService;

    private final ServicePointService servicePoint;

    private final RequestEventPublisher requestEventPublisher;

    @Transactional
    public PatientScanRegister savePatientResults(PatientScanRegisterData patientScanRegData, final String visitNo) {
        PatientScanRegister patientScanReg = patientScanRegData.fromData();
        String transactionId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        patientScanRegData.setTransactionId(transactionId);
        patientScanReg.setTransactionId(transactionId);
        patientScanReg.setIsWalkin(patientScanRegData.getIsWalkin());

        if (visitNo != null && !patientScanReg.getIsWalkin()) {
            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
            patientScanReg.setVisit(visit);
            patientScanReg.setPatientName(visit.getPatient().getFullName());
            patientScanReg.setPatientNo(visit.getPatient().getPatientNumber());
        } else {
            patientScanReg.setPatientName(patientScanRegData.getPatientName());
            patientScanReg.setPatientNo(patientScanRegData.getPatientNumber());
        }

        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientScanRegData.getRequestedBy());
        if (emp.isPresent()) {
            patientScanReg.setRequestedBy(emp.get());
        }

//        if (requestId != null) {
//            Optional<DoctorRequest> request = doctorRequestRepository.findById(requestId);
//            if (request.isPresent()) {
//                patientScanReg.setRequest(request.get());
//            }
//
//        }
        String accessionNo = sequenceNumberService.next(1L, Sequences.RadiologyNumber.name());
        patientScanReg.setAccessNo(accessionNo);

        if (!patientScanRegData.getItemData().isEmpty()) {
            List<PatientScanTest> patientScanTest = new ArrayList<>();
            for (ScanItemData id : patientScanRegData.getItemData()) {
                Item i = itemService.findItemWithNoFoundDetection(id.getItemCode());
                RadiologyTest labTestType = radiologyConfigService.findScanByItem(i);
                PatientScanTest pte = new PatientScanTest();
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setRadiologyTest(labTestType);
                pte.setStatus(ScanTestState.Scheduled);
                pte.setPaid(patientScanReg.getPaymentMode().equals("Cash") ? Boolean.FALSE : Boolean.TRUE);
                pte.setMedic(employeeService.findEmployeeById(id.getMedicId()));
//                Employee medic = employeeService.findEmployeeById(id.getMedicId());
//                if (medic.isPresent()) {
//                    pte.setMedic(employeeService.findEmployeeById(id.getMedicId()));
//                }
                if (id.getRequestItemId() != null) {
                    Optional<DoctorRequest> request = doctorRequestRepository.findById(id.getRequestItemId());
                    if (request.isPresent()) {
                        pte.setRequest(request.get());
                        request.get().setFulfillerStatus(FullFillerStatusType.Fulfilled);
                    }
                }
                patientScanTest.add(pte);
            }
            patientScanReg.addPatientScans(patientScanTest);

        }
        PatientScanRegister scanRegister = patientradiologyRepository.save(patientScanReg);
        PatientBill bill = toBill(scanRegister);
        billService.save(bill);
        requestEventPublisher.publishUpdateEvent(DoctorRequestData.RequestType.Radiology);
        return scanRegister;
    }

    private PatientBill toBill(PatientScanRegister data) {
        //get the service point from store
        ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Radiology);
        PatientBill patientbill = new PatientBill();

        if (!data.getIsWalkin()) {
            patientbill.setVisit(data.getVisit());
            patientbill.setPatient(data.getVisit().getPatient());
            patientbill.setWalkinFlag(Boolean.FALSE);
        } else {
            patientbill.setReference(data.getPatientNo());
            patientbill.setOtherDetails(data.getPatientName());
            patientbill.setWalkinFlag(Boolean.TRUE);
        }
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);

        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

        patientbill.setBillNumber(bill_no);
        List<PatientBillItem> lineItems = data.getPatientScanTest()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();
                    Item item = itemService.findByItemCodeOrThrow(lineData.getRadiologyTest().getItem().getItemCode());

                    billItem.setTransactionId(data.getTransactionId());
                    billItem.setServicePoint(ServicePointType.Radiology.name());
                    if (lineData.getMedic() != null) {
                        billItem.setMedicId(lineData.getMedic().getId());
                    }
                    billItem.setItem(item);
                    billItem.setRequestReference(lineData.getId());
//                    if (lineData.getRequest() != null) {
//                        
//                    }
                    billItem.setPrice(lineData.getTestPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getTestPrice() * lineData.getQuantity());
                    billItem.setBalance(lineData.getTestPrice() * lineData.getQuantity());
                    billItem.setServicePoint(servicePoint.getName());
                    billItem.setServicePointId(servicePoint.getId());
                    billItem.setStatus(BillStatus.Draft);
                    billItem.setPaid(Boolean.FALSE);
                    billItem.setBillingDate(data.getBillingDate());

                    return billItem;
                })
                .collect(Collectors.toList());
        patientbill.addBillItems(lineItems);
        return patientbill;
    }

    @Transactional
    public RadiologyResult saveRadiologyResult(RadiologyResultData data) {
        RadiologyResult radiologyResult = data.fromData();
        PatientScanTest patientScanTest = findPatientRadiologyTestByIdWithNotFoundDetection(data.getTestId());

        patientScanTest.setStatus(data.getStatus());
        radiologyResult.setPatientScanTest(patientScanTest);
        radiologyResult.setStatus(ScanTestState.Completed);
        patientScanTest.setRadiologyResult(radiologyResult);
        patientScanTest.setEntryDateTime(LocalDateTime.now());
        return pscanRepository.save(patientScanTest).getRadiologyResult();
//        return radiologyResultRepo.save(radiologyResult);
    }

    @Transactional
    public RadiologyResult updateRadiologyResult(Long id, MultipartFile file, RadiologyResultData data) {
        RadiologyResult radiologyResult = findResultsByIdWithNotFoundDetection(id);
        radiologyResult.setComments(data.getComments());
        radiologyResult.setNotes(data.getTemplateNotes());
        PatientScanTest patientScanTest = findPatientRadiologyTestByIdWithNotFoundDetection(data.getTestId());
        radiologyResult.setPatientScanTest(patientScanTest);
        radiologyResult.setResultsDate(data.getResultsDate());
        radiologyResult.setStatus(data.getStatus());
        radiologyResult.setVoided(data.getVoided());
        patientScanTest.setRadiologyResult(radiologyResult);
        pscanRepository.save(patientScanTest);
        return radiologyResultRepo.save(radiologyResult);
    }

    public RadiologyResult findResultsByIdWithNotFoundDetection(Long id) {
        return radiologyResultRepo.findById(id).orElseThrow(() -> APIException.notFound("Results identified by id {0} not found ", id));
    }

    @Transactional
    public PatientScanTest updatePatientScanTest(Long id, PatientScanTestData data) {
        PatientScanTest radiologyTest = findPatientRadiologyTestByIdWithNotFoundDetection(id);
        radiologyTest.setComments(data.getComments());
        radiologyTest.setDone(data.getDone());
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(data.getDoneBy());
        if (emp.isPresent()) {
            radiologyTest.setMedic(emp.get());
        }
        radiologyTest.setQuantity(data.getQuantity());
        radiologyTest.setStatus(data.getStatus());
        radiologyTest.setTestPrice(data.getTestPrice());
        return pscanRepository.save(radiologyTest);
    }

    @Transactional
    public PatientScanTest uploadScanImage(Long id, MultipartFile file) {
        PatientScanTest radiologyTest = findPatientRadiologyTestByIdWithNotFoundDetection(id);

        if (file != null) {
            ServicePoint servPoint = servicePoint.getServicePointByType(ServicePointType.Radiology);
            DocumentData documentData = new DocumentData();
            documentData.setDocumentNumber(radiologyTest.getPatientScanRegister().getAccessNo());
            documentData.setDocumentType(DocumentType.Scan);

            documentData.setDocfile(file);
            documentData.setServicePointId(servPoint.getId());
            Document document = fileStoerageService.documentUpload(documentData);
            radiologyTest.setDocument(document);
        }
        return pscanRepository.save(radiologyTest);
    }

    public Page<RadiologyResult> findAllRadiologyResults(String visitNumber, String patientNumber, String scanNumber, Boolean walkin, ScanTestState status, String orderNo, DateRange range, String search, Pageable pgbl) {
        Specification spec = RadiologyResultSpecification.createSpecification(patientNumber, orderNo, visitNumber, walkin, status, range, search);
        return radiologyResultRepo.findAll(spec, pgbl);

    }

    public PatientScanTest findPatientRadiologyTestByIdWithNotFoundDetection(Long id) {
        return pscanRepository.findById(id).orElseThrow(() -> APIException.notFound("Patient results identified by id {0} not found ", id));
    }

    public Page<PatientScanTest> findAllTests(String PatientNumber, String search, String scanNo, ScanTestState status, String visitId, DateRange range, Boolean isWalkin, Pageable pgbl) {
        Specification spec = RadiologyTestSpecification.createSpecification(PatientNumber, scanNo, visitId, isWalkin, status, range, search);
        return pscanRepository.findAll(spec, pgbl);
    }

    public PatientScanRegister findPatientRadiologyTestByAccessNoWithNotFoundDetection(String accessNo) {
        return patientradiologyRepository.findByAccessNo(accessNo).orElseThrow(() -> APIException.notFound("Patient Scan identified by scanN Number {0} not found ", accessNo));
    }

    public List<PatientScanRegister> findPatientScanRegisterByVisit(final Visit visit) {
        return patientradiologyRepository.findByVisit(visit);
    }

    @Transactional
    public Page<PatientScanRegister> findAll(String PatientNumber, String scanNo, String visitId, ScanTestState status, DateRange range, Pageable pgbl) {
        Specification spec = RadiologyRegisterSpecification.createSpecification(PatientNumber, scanNo, visitId, status, Boolean.FALSE, range);
        return patientradiologyRepository.findAll(spec, pgbl);
    }

    public PatientScanRegister findPatientScanRegisterByIdWithNotFoundDetection(Long id) {
        return patientradiologyRepository.findById(id).orElseThrow(() -> APIException.notFound("Patient Scan identified by Id{0} not found ", id));
    }

    public List<RadiologyResultData> getLabResultDataByVisit(Visit visit) {
        return getResultByVisit(visit)
                .stream()
                .map(x -> x.toData())
                .collect(Collectors.toList());
    }

    public List<RadiologyResult> getResultByVisit(Visit visit) {
        return radiologyResultRepo.findByVisit(visit);
    }

    public List<PatientScanTest> getPatientScansTestByVisit(String visitNumber) {
        return registerTestRepository.findByVisit(visitNumber);
    }

    public List<PatientScanTest> findScanResultsByVisit(final Visit visit) {
        List<PatientScanRegister> scanTestFile = findPatientScanRegisterByVisit(visit);
        List<PatientScanTest> patientScansDone = new ArrayList<>();
        //find patient scans by labTestFile
        scanTestFile.forEach((scanFile) -> {
            scanFile.getPatientScanTest().forEach((testDone) -> {
                patientScansDone.add(testDone);
            });
        });
        return patientScansDone;
    }

    public void voidRadiologyRegister(Long id) {
        PatientScanRegister requests = findPatientScanRegisterByIdWithNotFoundDetection(id);
        requests.setVoided(Boolean.TRUE);
        patientradiologyRepository.save(requests);
    }

    @Transactional
    public void markRadiologyResultStatusAsRead(RadiologyResult radiologyResult) {
        radiologyResult.setResultRead(Boolean.TRUE);
        radiologyResultRepo.save(radiologyResult);
    }
}
