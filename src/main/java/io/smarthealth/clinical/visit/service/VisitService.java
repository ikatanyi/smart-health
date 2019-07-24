/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.data.VisitDTO;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Visit> fetchVisitByPatientNumber(String patientNumber, final Pageable pageable) {
        Patient patient = patientRepository.findByPatientNumber(patientNumber).get();
        Page<Visit> visits = visitRepository.findByPatient(patient, pageable);
        return visits;
    }

    public VisitDTO createAVisit(final VisitDTO visitDTO, final String patientNumber) {
        //Fetch patient entity 
        Patient patient = this.findPatientEntityOrThrow(patientNumber);
        Visit visit = VisitDTO.map(visitDTO);
        visit.setPatient(patient);
        visitRepository.saveAndFlush(visit);
        return visitDTO;
    }

    public String updateVisit(final String visitNumber, final VisitDTO visitDTO) {
        findVisitEntityOrThrow(visitNumber);
        //validate and fetch patient
        Patient patient = findPatientEntityOrThrow(visitDTO.getPatientNumber());

        Visit visitEntity = VisitDTO.map(visitDTO);
        visitEntity.setPatient(patient);
        visitRepository.save(visitEntity);
        return visitDTO.getVisitNumber();
    }

    private Patient findPatientEntityOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }

    private Visit findVisitEntityOrThrow(String visitNumber) {
        return this.visitRepository.findByVisitNumber(visitNumber)
                .orElseThrow(() -> APIException.notFound("Visit Number {0} not found.", visitNumber));
    }

}
