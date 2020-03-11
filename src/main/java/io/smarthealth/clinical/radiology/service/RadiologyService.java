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
import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.data.ScanItemData;
import io.smarthealth.clinical.radiology.domain.PatientRadiologyTestRepository;
import io.smarthealth.clinical.radiology.domain.PatientScanRegister;
import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.PatientScanTestRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.radiology.domain.specification.RadiologySpecification;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RadiologyRepository radiologyRepository;

    private final PatientRadiologyTestRepository patientradiologyRepository;

    private final DoctorsRequestRepository doctorRequestRepository;

    private final PatientScanTestRepository pscanRepository;

    private final BillingService billService;

    private final ServicePointService servicePointService;

    private final EmployeeService employeeService;

    private final PatientService patientservice;

    private final ItemService itemService;

    private final VisitService visitService;

    private final SequenceService seqService;

    private final SequenceNumberService sequenceNumberService;

    @Transactional
    public List<RadiologyTest> createRadiologyTest(List<RadiologyTestData> radiolgyTestData) {
        try {
            List<RadiologyTest> radiologyTests = radiolgyTestData
                    .stream()
                    .map((radiologyTest) -> {
                        RadiologyTest test = RadiologyTestData.map(radiologyTest);
                        Optional<Item> item = itemService.findByItemCode(radiologyTest.getItemCode());
                        if (item.isPresent()) {
                            test.setItem(item.get());
                        }
                        return test;
                    })
                    .collect(Collectors.toList());
            return radiologyRepository.saveAll(radiologyTests);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating RadiologyItem ", e.getMessage());
        }
    }

    @Transactional
    public RadiologyTest UpdateRadiologyTest(RadiologyTestData radiolgyTestData) {
        RadiologyTest radiologyTest = this.getById(radiolgyTestData.getId());
        RadiologyTest test = RadiologyTestData.map(radiolgyTestData);
        test.setId(radiologyTest.getId());
        Optional<Item> item = itemService.findByItemCode(radiolgyTestData.getItemCode());
        if (item.isPresent()) {
            test.setItem(item.get());
        }

        return radiologyRepository.save(test);

    }

    public RadiologyTest findScanByItem(final Item item) {
        return radiologyRepository.findByItem(item).orElseThrow(() -> {
            return APIException.notFound("Radiology Test not Registered in Radiology Department");
        });
    }

    @Transactional
    public RadiologyTest getById(Long id) {
        return radiologyRepository.findById(id).orElseThrow(() -> APIException.notFound("Radiology Test identified by {0} not found", id));
    }

    @Transactional
    public Page<RadiologyTest> findAll(Pageable pgbl) {
        return radiologyRepository.findAll(pgbl);
    }

    @Transactional
    public PatientScanRegister savePatientResults(PatientScanRegisterData patientScanRegData, final String visitNo, final Long requestId) {
        PatientScanRegister patientScanReg = PatientScanRegisterData.map(patientScanRegData);
        String transactionId = sequenceNumberService.next(1L, Sequences.RadiologyNumber.name());
        patientScanReg.setTransactionId(transactionId);
        patientScanReg.setIsWalkin(patientScanRegData.getIsWalkin());
        
        if (visitNo != null) {
            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
            patientScanReg.setVisit(visit);
            patientScanReg.setPatient(visit.getPatient());
        } 
        
        
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientScanRegData.getRequestedBy());
        if (emp.isPresent()) {
            patientScanReg.setRequestedBy(emp.get());
        }

        if (requestId != null) {
            Optional<DoctorRequest> request = doctorRequestRepository.findById(requestId);
            if (request.isPresent()) {
                patientScanReg.setRequest(request.get());
            }

        }
        if (patientScanRegData.getAccessionNo() == null || patientScanRegData.getAccessionNo().equals("")) {
            String accessionNo = sequenceNumberService.next(1L, Sequences.RadiologyNumber.name());
            patientScanReg.setAccessNo(accessionNo);
        }

        if (!patientScanRegData.getItemData().isEmpty()) {
            List<PatientScanTest> patientScanTest = new ArrayList<>();
            for (ScanItemData id : patientScanRegData.getItemData()) {
                Item i = itemService.findItemWithNoFoundDetection(id.getItemCode());
                RadiologyTest labTestType = findScanByItem(i);
                PatientScanTest pte = new PatientScanTest();
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setRadiologyTest(labTestType);
                Optional<Employee> medic = employeeService.findEmployeeByStaffNumber(id.getMedicId());
                if (emp.isPresent()) {
                    pte.setMedic(emp.get());
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
        patientbill.setPatient(data.getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getBalance());

        patientbill.setReferenceNo(data.getAccessNo());
        patientbill.setPaymentMode(data.getPaymentMode());
//        patientbill.setTransactionId(data.getTransactionId());
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
                    billItem.setPrice(lineData.getTestPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getTestPrice() * lineData.getQuantity());
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
    public PatientScanTest updateRadiologyResult(PatientScanTest r) {
        return pscanRepository.save(r);
    }

    public PatientScanTest findResultsByIdWithNotFoundDetection(Long id) {
        return pscanRepository.findById(id).orElseThrow(() -> APIException.notFound("Results identified by id {0} not found ", id));
    }

    public PatientScanRegister findScansByIdWithNotFoundDetection(String accessNo) {
        return patientradiologyRepository.findByAccessNo(accessNo).orElseThrow(() -> APIException.notFound("Patient Scan identified by scanN Number {0} not found ", accessNo));
    }

    public List<PatientScanRegister> findPatientScanRegisterByVisit(final Visit visit) {
        return patientradiologyRepository.findByVisit(visit);
    }

    @Transactional
    public Page<PatientScanRegister> findAll(String PatientNumber, String scanNo, String visitId, DateRange range, Pageable pgbl) {
        Specification spec = RadiologySpecification.createSpecification(PatientNumber, scanNo, visitId, range);
        return patientradiologyRepository.findAll(spec, pgbl);
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
}
