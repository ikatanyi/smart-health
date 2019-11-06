/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.PatientTestData;
import io.smarthealth.clinical.lab.domain.LabTestRepository;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class LabResultsService {

    private final LabTestRepository PtestsRepository;

    private final ModelMapper modelMapper;
    
    private final VisitRepository visitRepository;

    /*
    a. Create a new department
    b. Read all departments 
    c. Read department by Id
    c. Update department
     */

    public LabResultsService(LabTestRepository PtestsRepository, ModelMapper modelMapper, VisitRepository visitRepository) {
        this.PtestsRepository = PtestsRepository;
        this.modelMapper = modelMapper;
        this.visitRepository = visitRepository;
    }
    
    
    @Transactional
    public PatientTestData savePatientResults(PatientTestData testResults) {
         Visit visit = visitRepository.findByVisitNumber(testResults.getVisitNumber())
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", testResults.getVisitNumber()));

//        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), testResults.getPatientNumber())) {
//            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
//        }
        
        
        PatientLabTest patienttestsEntity = convertDataToPatientTestsData(testResults);
        patienttestsEntity.setVisit(visit);
        patienttestsEntity.setPatient(visit.getPatient());
        PatientLabTest patientTests = PtestsRepository.save(patienttestsEntity);
        return convertPatientTestToData(patientTests);
    }
    
    
    
    
    public Page<PatientTestData> fetchAllPatientTests(String patientNumber, String visitNumber, String status, Pageable pgbl) {
        Page<PatientTestData> ptests = PtestsRepository.findByPatientNumberAndVisitNumberAndStatus(patientNumber, visitNumber, status,pgbl).map(p -> convertPatientTestToData(p));
        return ptests;
    }
    
    public Optional<PatientTestData> fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).map(p->convertPatientTestToData(p));
    }
    

    public void deleteById(Long id) {
        PtestsRepository.deleteById(id);
    }

    public PatientTestData convertPatientTestToData(PatientLabTest patientTests) {
        PatientTestData patientsdata = modelMapper.map(patientTests, PatientTestData.class);
        return patientsdata;
    }
    
    public PatientLabTest convertDataToPatientTestsData(PatientTestData patientTestsData) {
        PatientLabTest patienttests = modelMapper.map(patientTestsData, PatientLabTest.class);
        return patienttests;
    }

}
