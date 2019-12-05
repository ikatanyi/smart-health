/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.service;
 
import io.smarthealth.billing.service.PatientBillService;
import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.data.ProcedureItemData;
import io.smarthealth.clinical.procedure.data.ProcedureTestData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureRegister;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTestRepository;
import io.smarthealth.clinical.procedure.domain.ProcedureRepository;
import io.smarthealth.clinical.procedure.domain.ProcedureTest;
import io.smarthealth.clinical.procedure.domain.ProcedureTestRepository;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.DoctorsRequestRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.service.VisitService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.service.SequenceService;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class ProcedureService {

    @Autowired
    ProcedureRepository procedureRepository;
    @Autowired
    PatientProcedureTestRepository patientprocedureRepository;
    @Autowired
    DoctorsRequestRepository doctorRequestRepository;
    @Autowired
    ProcedureTestRepository procTestRepository;

    private final EmployeeService employeeService;
    private final PatientService patientservice;
    private final PatientBillService billService;
    private final ItemService itemService;
    private final VisitService visitService;
    private final SequenceService seqService;


    @Transactional
    public List<ProcedureTest> createProcedureTest(List<ProcedureTestData> procedureTestData) {
        try {
            List<ProcedureTest> procedureTests = procedureTestData
                    .stream()
                    .map((procedureTest) -> {
                        ProcedureTest test = ProcedureTestData.map(procedureTest);
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
    public ProcedureTest UpdateProcedureTest(ProcedureTestData procedureTestData) {
        ProcedureTest procedureTest = this.getById(procedureTestData.getId());
        ProcedureTest test = ProcedureTestData.map(procedureTestData);
        test.setId(procedureTest.getId());
        Optional<Item> item = itemService.findByItemCode(procedureTestData.getItemCode());
        if (item.isPresent()) {
            test.setItem(item.get());
        }

        return procedureRepository.save(test);

    }
    
    public ProcedureTest findProcedureByItem(final Item item) {
        return procedureRepository.findByItem(item).orElseThrow(() -> {
            return APIException.notFound("Procedure Test not Registered in Procedure Department");
        });
    }

    @Transactional
    public ProcedureTest getById(Long id) {
        return procedureRepository.findById(id).orElseThrow(() -> APIException.notFound("Procedure Test identified by {0} not found", id));
    }
    
    @Transactional
    public Page<ProcedureTest> findAll(Pageable pgbl) {
        return procedureRepository.findAll(pgbl);
    }
    
    @Transactional
    public PatientProcedureRegister savePatientResults(PatientProcedureRegisterData patientProcRegData, final String visitNo, final Long requestId) {
        PatientProcedureRegister patientProcReg = PatientProcedureRegisterData.map(patientProcRegData);
        if (visitNo != null) {
            Visit visit = visitService.findVisitEntityOrThrow(visitNo);
            patientProcReg.setVisit(visit);
            patientProcReg.setPatient(visit.getPatient());
        } else {
            throw APIException.badRequest("A fully fledged visit session MUST be available", "");
        }
        Optional<Employee> emp = employeeService.findEmployeeByStaffNumber(patientProcRegData.getRequestedBy());
        if (emp.isPresent()) {
            patientProcReg.setRequestedBy(emp.get());
        }

        if (requestId != null) {
            Optional<DoctorRequest> request = doctorRequestRepository.findById(requestId);
            if (request.isPresent()) {
                patientProcReg.setRequest(request.get());
            }

        }
        if (patientProcRegData.getAccessionNo() == null || patientProcRegData.getAccessionNo().equals("")) {
            String accessionNo = seqService.nextNumber(SequenceType.ProcedureNumber);
            patientProcReg.setAccessNo(accessionNo);
        }

        //PatientTestRegister savedPatientTestRegister = patientRegRepository.save(patientTestReg);
        if (!patientProcRegData.getItemData().isEmpty()) {
            List<PatientProcedureTest> patientProcTest = new ArrayList<>();
            for (ProcedureItemData id : patientProcRegData.getItemData()) {
                Item i = itemService.findItemWithNoFoundDetection(id.getItemCode());
                ProcedureTest labTestType = findProcedureByItem(i);
                PatientProcedureTest pte = new PatientProcedureTest();
                pte.setStatus(ProcedureTestState.valueOf(id.getStatus()));
                pte.setTestPrice(id.getItemPrice());
                pte.setQuantity(id.getQuantity());
                pte.setProcedureTest(labTestType);
                
                patientProcTest.add(pte);
            }
            patientProcReg.addPatientProcedures(patientProcTest);

        }
        return patientprocedureRepository.save(patientProcReg);
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
    
    public List<PatientProcedureRegister> findPatientProcedureRegisterByVisit(final Visit visit) {
        return patientprocedureRepository.findByVisit(visit);
    }

    public List<PatientProcedureTest> findProcedureResultsByVisit(final Visit visit) {
        List<PatientProcedureRegister> procedureTestFile = findPatientProcedureRegisterByVisit(visit);
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
