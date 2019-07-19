/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.infrastructure.utility.APIException;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonAddressRepository;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.PersonContactRepository;
import io.smarthealth.organization.person.mapper.AddressDTO;
import io.smarthealth.organization.person.mapper.ContactDTO;
import io.smarthealth.organization.person.mapper.PatientDTO;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    @Autowired
    PersonContactRepository personContactRepository;
    @Autowired
    PersonAddressRepository personAddressRepository;

    public List<Patient> fetchAllPatients() {
        return patientRepository.findAll();
    }

    public Patient fetchPatientByIdentityNumber(Long patientId) {
        return patientRepository.getOne(patientId);
    }

    public Optional<PatientDTO> fetchPatientByPatientNumber(final String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber)
                .map(patientEntity -> {
                    final PatientDTO patient = PatientDTO.map(patientEntity);
                    //fetch patient addresses
                    final List<PersonAddress> personAddressEntity = personAddressRepository.findByPerson(patientEntity);
                    if (personAddressEntity != null) {
                        patient.setAddressDetails(
                                personAddressEntity
                                        .stream()
                                        .map(AddressDTO::map)
                                        .collect(Collectors.toList())
                        );
                    }

                    final List<PersonContact> contactDetailEntities = this.personContactRepository.findByPerson(patientEntity);
                    if (contactDetailEntities != null) {
                        patient.setContactDetails(
                                contactDetailEntities
                                        .stream()
                                        .map(ContactDTO::map)
                                        .collect(Collectors.toList())
                        );
                    }

                    return patient;
                });
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
