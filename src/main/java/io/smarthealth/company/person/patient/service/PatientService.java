/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.company.person.patient.service;

import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.company.person.patient.domain.Patient;
import io.smarthealth.company.person.patient.domain.PatientRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Simon.waweru
 */
@Service
public class PatientService {

    @Autowired
    PatientRepository patientRepository;

    public List<Patient> fetchAllPatients() {
        return patientRepository.findAll();
    }

    public Patient fetchPatientByIdentityNumber(Long patientId) {
        return patientRepository.getOne(patientId);
    }

    public Patient fetchPatientByPatientNumber(String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber).get();
    }

    public Patient createPatient(final Patient patient) {
        return patientRepository.save(patient);
    }

    public String updatePatient(String patientNumber, Patient patient) {
        try {
            final Patient patientEntity = findPatientEntityOrThrow(patient.getPatientNumber());

            patientEntity.setGivenName(patient.getGivenName());
            patientEntity.setMiddleName(patient.getMiddleName());
            patientEntity.setSurname(patient.getSurname());
            patientEntity.setDateOfBirth(patient.getDateOfBirth());

            patientEntity.setLastModifiedOn(Instant.now());

            this.patientRepository.save(patientEntity);

            return patient.getPatientNumber();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestClientException("Error updating patient number" + patientNumber);
        }
    }

    private Patient findPatientEntityOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }
}
