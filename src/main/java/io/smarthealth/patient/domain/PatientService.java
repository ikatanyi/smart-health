/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.patient.domain;

import io.smarthealth.common.utility.APIException;
import io.smarthealth.common.utility.CrudErrorException;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new CrudErrorException(e.getMessage());
        }
    }

    private Patient findPatientEntityOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }
}
