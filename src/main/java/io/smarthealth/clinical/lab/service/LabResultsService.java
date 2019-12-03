/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.service;

import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.lab.domain.Results;
import io.smarthealth.clinical.lab.domain.ResultsRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class LabResultsService {

    @Autowired
    LabService labService;

    @Autowired
    ResultsRepository resultsRepository;

    @Transactional
    public Results updateLabResult(Results r) {
        return resultsRepository.save(r);
    }

    public Results findResultsByIdWithNotFoundDetection(Long id) {
        return resultsRepository.findById(id).orElseThrow(() -> APIException.notFound("Results identified by id {0} not found ", id));
    }

    public List<PatientLabTest> findLabResultsByVisit(final Visit visit) {
        List<PatientTestRegister> labTestFile = labService.findPatientTestRegisterByVisit(visit);
        List<PatientLabTest> patientLabTestsDone = new ArrayList<>();
        //find patient tests by labTestFile
        for (PatientTestRegister testFile : labTestFile) {
            testFile.getPatientLabTest().forEach((testDone) -> {
                patientLabTestsDone.add(testDone);
            });
        }
        return patientLabTestsDone;
    }
}
