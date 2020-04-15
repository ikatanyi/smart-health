/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import io.smarthealth.clinical.radiology.domain.RadiologyRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.RadiologyResultRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyRegisterSpecification;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyResultSpecification;
import io.smarthealth.clinical.radiology.domain.specification.RadiologyTestSpecification;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.time.LocalDate;
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
            patientScanReg.setPatientName(patientScanReg.getPatientNo());
            patientScanReg.setPatientNo(patientScanReg.getPatientNo());
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
                Optional<Employee> medic = employeeService.findEmployeeByStaffNumber(id.getMedicId());
                if (medic.isPresent()) {
                    pte.setMedic(medic.get());
                }
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
        return scanRegister;
    }

    private PatientBill toBill(PatientScanRegister data) {
        //get the service point from store
        ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Radiology);
        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(data.getVisit());
        patientbill.setVisit(data.getVisit());
        if (data.getVisit() != null) {
            patientbill.setPatient(data.getVisit().getPatient());
        }
          if (!data.getIsWalkin()) { 
            patientbill.setWalkinFlag(Boolean.FALSE);
        } else {
            patientbill.setReference(data.getPatientNo());
            patientbill.setOtherDetails(data.getPatientName());
            patientbill.setWalkinFlag(Boolean.TRUE);
        }
        patientbill.setBillingDate(data.getBillingDate());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getBalance());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);
        patientbill.setBillingDate(LocalDate.now());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());

        patientbill.setBillNumber(bill_no);
        List<PatientBillItem> lineItems = data.getPatientScanTest()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();
                    Item item = itemService.findByItemCodeOrThrow(lineData.getRadiologyTest().getItem().getItemCode());

                    billItem.setTransactionId(data.getTransactionId());
                    billItem.setServicePoint(ServicePointType.Procedure.name());
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
        return pscanRepository.save(patientScanTest).getRadiologyResult();
//        return radiologyResultRepo.save(radiologyResult);
    }

    @Transactional
    public RadiologyResult updateRadiologyResult(Long id, RadiologyResultData data) {
        RadiologyResult radiologyResult = findResultsByIdWithNotFoundDetection(id);
        radiologyResult.setComments(data.getComments());
        radiologyResult.setImagePath(data.getImagePath());
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

    public Page<RadiologyResult> findAllRadiologyResults(String visitNumber, String patientNumber, String scanNumber, Boolean walkin, ScanTestState status, String orderNo, DateRange range, String search, Pageable pgbl) {
        Specification spec = RadiologyResultSpecification.createSpecification(patientNumber, orderNo, visitNumber, walkin, status, range,search);
        return radiologyResultRepo.findAll(spec, pgbl);

    }

    public PatientScanTest findPatientRadiologyTestByIdWithNotFoundDetection(Long id) {
        return pscanRepository.findById(id).orElseThrow(() -> APIException.notFound("Patient results identified by id {0} not found ", id));
    }

    public Page<PatientScanTest> findAllTests(String PatientNumber, String search, String scanNo, ScanTestState status, String visitId, DateRange range, Boolean isWalkin, Pageable pgbl) {
        Specification spec = RadiologyTestSpecification.createSpecification(PatientNumber, scanNo, visitId, isWalkin, status, range, search);
        return pscanRepository.findAll(spec, pgbl);
    }

    public PatientScanRegister findPatientRadiologyTestByIdWithNotFoundDetection(String accessNo) {
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

    public List<PatientScanTest> findScanResultsByVisit(final Visit visit) {
        List<PatientScanRegister> scanTestFile = findPatientScanRegisterByVisit(visit);
        List<PatientScanTest> patientScansDone = new ArrayList<>();
        //find patient scans by labTestFile
        for (PatientScanRegister scanFile : scanTestFile) {
            scanFile.getPatientScanTest().forEach((testDone) -> {
                patientScansDone.add(testDone);
            });
        }
        return patientScansDone;
    }

    public void voidRadiologyRegister(Long id) {
        PatientScanRegister requests = findPatientScanRegisterByIdWithNotFoundDetection(id);
        requests.setVoided(Boolean.TRUE);
        patientradiologyRepository.save(requests);
    }
}
