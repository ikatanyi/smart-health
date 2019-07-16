/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.visit.domain;

import io.smarthealth.patient.domain.Patient;
import io.smarthealth.patient.domain.PatientRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;

    public VisitService(VisitRepository visitRepository, PatientRepository patientRepository) {
        this.visitRepository = visitRepository;
        this.patientRepository = patientRepository;
    }

    public Page<Visit> fetchVisitByPatientNumber(String patientNumber) {
        Patient patient = patientRepository.findByPatientNumber(patientNumber).get();
        Page<Visit> visits = visitRepository.findByPatient(patient);
        return visits;
    }

}
