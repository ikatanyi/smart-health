/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.accounting.billing.service.BillingService;
import io.smarthealth.accounting.pricelist.domain.PriceList;
import io.smarthealth.accounting.pricelist.service.PricelistService;
import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.data.ProcedureItemData;
import io.smarthealth.clinical.procedure.data.ProcedureData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTestRepository;
import io.smarthealth.clinical.procedure.domain.ProcedureRepository;
import io.smarthealth.clinical.procedure.domain.Procedure;
import io.smarthealth.clinical.procedure.domain.ProcedureTestRepository;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.radiology.domain.specification.RadiologySpecification;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
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
public class ProcedureService {

    private final ProcedureRepository procedureRepository;

    private final PatientProcedureTestRepository patientprocedureRepository;

    private final DoctorsRequestRepository doctorRequestRepository;

    private final ProcedureTestRepository procTestRepository;

    private final EmployeeService employeeService;
    private final ItemService itemService;
    private final VisitService visitService;
    private final SequenceService seqService;
    private final SequenceNumberService sequenceNumberService;
    private final BillingService billingService;
    private final ServicePointService servicePointService;
    private final PricelistService pricelistService;

    @Transactional
    public List<Procedure> createProcedureTest(List<ProcedureData> procedureTestData) {
        try {
            List<Procedure> procedureTests = procedureTestData
                    .stream()
                    .map((procedureTest) -> {
                        Procedure test = ProcedureData.map(procedureTest);
                        Optional<Item> item = itemService.findByItemCode(procedureTest.getItemCode());
                        if (item.isPresent()) {
                            test.setItem(item.get());
                            
                        }
                        return test;
                    })
                    .collect(Collectors.toList());
            return procedureRepository.saveAll(procedureTests);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating ProcedureItem ", e.getMessage());
        }
    }

    @Transactional
    public Procedure UpdateProcedureTest(ProcedureData procedureTestData) {
        Procedure procedureTest = this.getById(procedureTestData.getId());
        Procedure test = ProcedureData.map(procedureTestData);
        test.setId(procedureTest.getId());
        Optional<Item> item = itemService.findByItemCode(procedureTestData.getItemCode());
        if (item.isPresent()) {
            test.setItem(item.get());
        }

        return procedureRepository.save(test);

    }

    public Procedure findProcedureByItem(final Item item) {
        return procedureRepository.findByItem(item).orElseThrow(() -> {
            return APIException.notFound("Procedure Test not Registered in Procedure Department");
        });
    }

    @Transactional
    public Procedure getById(Long id) {
        return procedureRepository.findById(id).orElseThrow(() -> APIException.notFound("Procedure Test identified by {0} not found", id));
    }

    @Transactional
    public Page<Procedure> findAll(Pageable pgbl) {
        return procedureRepository.findAll(pgbl);
    }

    @Transactional
    public PatientProcedureRegister savePatientResults(PatientProcedureRegisterData patientProcRegData, final String visitNo, final Long requestId) {
        PatientProcedureRegister patientProcReg = PatientProcedureRegisterData.map(patientProcRegData);
        String transactionId = sequenceNumberService.next(1L, Sequences.Procedure.name());
        patientProcReg.setTransactionId(transactionId);
        
        if (visitNo != null) {
            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
            patientProcReg.setVisit(visit);
            patientProcReg.setPatientName(visit.getPatient().getFullName());
            patientProcReg.setPatientNo(visit.getPatient().getPatientNumber());
        } 
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientProcRegData.getRequestedBy());
        if (emp.isPresent()) {
            patientProcReg.setRequestedBy(emp.get());
        }

        if (requestId != null) {
            Optional<DoctorRequest> request = doctorRequestRepository.findById(requestId);
            if (request.isPresent()) {
                patientProcReg.setRequest(request.get());
                request.get().setFulfillerStatus("fulfilled");
            }

        }
        if (patientProcRegData.getAccessionNo() == null || patientProcRegData.getAccessionNo().equals("")) {
            String accessionNo = sequenceNumberService.next(1L, Sequences.Procedure.name());
            patientProcReg.setAccessNo(accessionNo);
        }

        //PatientTestRegister savedPatientTestRegister = patientRegRepository.save(patientTestReg);
       
        if (!patientProcRegData.getItemData().isEmpty()) {
            List<PatientProcedureTest> patientProcTest = new ArrayList<>();
            for (ProcedureItemData id : patientProcRegData.getItemData()) {
                Item item = itemService.findItemWithNoFoundDetection(id.getItemCode());
                PatientProcedureTest pte = new PatientProcedureTest();
                pte.setStatus(ProcedureTestState.Scheduled);
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setProcedureTest(item);
                Optional<Employee> employee = employeeService.findEmployeeByStaffNumber(id.getMedicId());
                if(employee.isPresent())
                   pte.setMedic(employee.get());
                patientProcTest.add(pte);
                
            }
             
            patientProcReg.addPatientProcedures(patientProcTest);     

        }
        PatientProcedureRegister savedProcedure=patientprocedureRepository.save(patientProcReg);
        
        PatientBill bill = toBill(savedProcedure);
        //save the bill
        billingService.save(bill);
        return savedProcedure;
    }
    
    private PatientBill toBill(PatientProcedureRegister data) {
        //get the service point from store
        ServicePoint servicePoint = servicePointService.getServicePointByType(ServicePointType.Procedure);
        PatientBill patientbill = new PatientBill();
        patientbill.setVisit(data.getVisit());
        if(data.getVisit()!=null)
            patientbill.setPatient(data.getVisit().getPatient());
        patientbill.setAmount(data.getAmount());
        patientbill.setDiscount(data.getDiscount());
        patientbill.setBalance(data.getAmount());
        
        patientbill.setReferenceNo(data.getAccessNo());
        patientbill.setPaymentMode(data.getPaymentMode());
        patientbill.setTransactionId(data.getTransactionId());
        patientbill.setStatus(BillStatus.Draft);
        patientbill.setBillingDate(LocalDate.now());
        String bill_no = sequenceNumberService.next(1L, Sequences.BillNumber.name());
        
        patientbill.setBillNumber(bill_no);
        List<PatientBillItem> lineItems = data.getPatientProcedureTest()
                .stream()
                .map(lineData -> {
                    PatientBillItem billItem = new PatientBillItem();
                    Item item = itemService.findByItemCodeOrThrow(lineData.getProcedureTest().getItemCode());
                   
                    billItem.setTransactionId(data.getTransactionId());
                    billItem.setServicePoint(ServicePointType.Procedure.name());
                    if(lineData.getMedic()!=null)
                       billItem.setMedicId(lineData.getMedic().getId());
                    billItem.setItem(item);
                    billItem.setPrice(lineData.getTestPrice());
                    billItem.setQuantity(lineData.getQuantity());
                    billItem.setAmount(lineData.getTestPrice()*lineData.getQuantity());
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
    public PatientProcedureTest updateProcedureResult(PatientProcedureTest r) {
        return procTestRepository.save(r);
    }

    public PatientProcedureTest findResultsByIdWithNotFoundDetection(Long id) {
        return procTestRepository.findById(id).orElseThrow(() -> APIException.notFound("Results identified by id {0} not found ", id));
    }

    public PatientProcedureRegister findProceduresByIdWithNotFoundDetection(String accessNo) {
        return patientprocedureRepository.findByAccessNo(accessNo).orElseThrow(() -> APIException.notFound("Patient Procedure identified by procedureN Number {0} not found ", accessNo));
    }

    public List<PatientProcedureRegister> findPatientProcedureRegisterByVisit(String VisitNumber) {
        Visit visit = visitService.findVisitEntityOrThrow(VisitNumber);
        return patientprocedureRepository.findByVisit(visit);
    }

    @Transactional
    public Page<PatientProcedureRegister> findAll(String PatientNumber, String scanNo, String visitId, DateRange range, Pageable pgbl) {
        Specification spec = RadiologySpecification.createSpecification(PatientNumber, scanNo, visitId, range);
        return patientprocedureRepository.findAll(spec, pgbl);
    }

    public List<PatientProcedureTest> findProcedureResultsByVisit(final String visitNumber) {
        List<PatientProcedureRegister> procedureTestFile = findPatientProcedureRegisterByVisit(visitNumber);
        List<PatientProcedureTest> patientProceduresDone = new ArrayList<>();
        //find patient procedures by labTestFile
        for (PatientProcedureRegister procedureFile : procedureTestFile) {
            procedureFile.getPatientProcedureTest().forEach((testDone) -> {
                patientProceduresDone.add(testDone);
            });
        }
        return patientProceduresDone;
    }
}
