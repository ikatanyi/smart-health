/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.LabTestData;
import io.smarthealth.clinical.lab.domain.LabTest;
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
import io.smarthealth.clinical.lab.domain.LabTestRepository;

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
    public LabTestData savePatientResults(LabTestData testResults) {
         Visit visit = visitRepository.findByVisitNumber(testResults.getVisitNumber())
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", testResults.getVisitNumber()));

        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), testResults.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }
        
        
        LabTest patienttestsEntity = convertDataToPatientTestsData(testResults);
        patienttestsEntity.setVisit(visit);
        patienttestsEntity.setPatient(visit.getPatient());
        LabTest patientTests = PtestsRepository.save(patienttestsEntity);
        return convertPatientTestToData(patientTests);
    }
    
    
    
    
    public Page<LabTestData> fetchAllPatientTests(String patientNumber, String visitNumber, String status, Pageable pgbl) {
        Page<LabTestData> ptests = PtestsRepository.findByPatientNumberAndVisitNumberAndStatus(patientNumber,status,pgbl).map(p -> convertPatientTestToData(p));
        return ptests;
    }
    
    public Optional<LabTestData> fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).map(p->convertPatientTestToData(p));
    }
    

    public void deleteById(Long id) {
        PtestsRepository.deleteById(id);
    }

    public LabTestData convertPatientTestToData(LabTest patientTests) {
        LabTestData patientsdata = modelMapper.map(patientTests, LabTestData.class);
        return patientsdata;
    }
    
    public LabTest convertDataToPatientTestsData(LabTestData patientTestsData) {
        LabTest patienttests = modelMapper.map(patientTestsData, LabTest.class);
        return patienttests;
    }

}
