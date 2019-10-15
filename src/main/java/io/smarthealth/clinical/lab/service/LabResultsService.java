/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.data.AnalyteData;
import io.smarthealth.clinical.lab.data.PatientTestData;
import io.smarthealth.clinical.lab.domain.Analyte;
import io.smarthealth.clinical.lab.domain.AnalyteRepository;
import io.smarthealth.clinical.lab.domain.PatientTestResultsRepository;
import io.smarthealth.clinical.lab.domain.PatientTests;
import io.smarthealth.clinical.lab.domain.Testtype;
import io.smarthealth.clinical.record.data.PatientTestsData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.organization.facility.service.*;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.DepartmentData;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.organization.facility.domain.DepartmentRepository;
import io.smarthealth.organization.facility.domain.Facility;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class LabResultsService {

    @Autowired
    PatientTestResultsRepository PtestsRepository;

    @Autowired
    ModelMapper modelMapper;
    
    @Autowired
    VisitRepository visitRepository;

    /*
    a. Create a new department
    b. Read all departments 
    c. Read department by Id
    c. Update department
     */
    @Transactional
    public Long savePatientResults(String visitNumber, PatientTestData testResults) {
         Visit visit = visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Patient Session with Visit Number : {0} not found.", visitNumber));;

        if (!StringUtils.equalsIgnoreCase(visit.getPatient().getPatientNumber(), testResults.getPatientNumber())) {
            throw APIException.badRequest("Invalid Patient Number! mismatch in Patient Visit's patient number");
        }
        
        
        PatientTests patienttestsEntity = convertDataToPatientTestsData(testResults);
        patienttestsEntity.setVisit(visit);
        patienttestsEntity.setPatient(visit.getPatient());
        return PtestsRepository.save(patienttestsEntity).getId();
    }
    
    
    
    
    public Page<PatientTestsData> fetchAllPatientTests(String patientNumber, String visitNumber, String status, Pageable pgbl) {
        Page<PatientTestsData> ptests = PtestsRepository.findByPatientNumberAndVisitNumberAndStatus(patientNumber,status,pgbl).map(p -> convertPatientTestsToData(p));
        return ptests;
    }
    
    public Optional<PatientTestsData> fetchPatientTestsById(Long id) {
        return PtestsRepository.findById(id).map(p->convertPatientTestsToData(p));
    }
    

    public void deleteById(Long id) {
        PtestsRepository.deleteById(id);
    }

    public PatientTestsData convertPatientTestsToData(PatientTests patientTests) {
        PatientTestsData patientsdata = modelMapper.map(patientTests, PatientTestsData.class);
        return patientsdata;
    }
    
    public PatientTests convertDataToPatientTestsData(PatientTestData patientTestsData) {
        PatientTests patienttests = modelMapper.map(patientTestsData, PatientTests.class);
        return patienttests;
    }

}
