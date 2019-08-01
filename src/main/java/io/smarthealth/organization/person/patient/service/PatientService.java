/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonAddressRepository;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.domain.PersonContactRepository;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public Page<Patient> fetchAllPatients(final Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Patient fetchPatientByIdentityNumber(Long patientId) {
        return patientRepository.getOne(patientId);
    }

    public Optional<PatientData> fetchPatientByPatientNumber(final String patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber)
                .map(patientEntity -> {
                    final PatientData patient = PatientData.map(patientEntity);
                    //fetch patient addresses
                    final List<PersonAddress> personAddressEntity = personAddressRepository.findByPerson(patientEntity);
                    if (personAddressEntity != null) {
                        patient.setAddressDetails(personAddressEntity
                                .stream()
                                .map(AddressData::map)
                                .collect(Collectors.toList())
                        );
                    }

                    final List<PersonContact> contactDetailEntities = this.personContactRepository.findByPerson(patientEntity);
                    if (contactDetailEntities != null) {
                        patient.setContactDetails(contactDetailEntities
                                .stream()
                                .map(ContactData::map)
                                .collect(Collectors.toList())
                        );
                    }

                    return patient;
                });
    }

//    public Patient createPatient(final PatientDTO patientDTO) {
//        Patient patient = PatientDTO.map(patientDTO);
//        return patientRepository.save(patient);
//    }
    @Transactional
    public String createPatient(final PatientData patient) {

        throwifDuplicatePatientNumber(patient.getPatientNumber());

        final Patient patientEntity = PatientData.map(patient);
        patientEntity.setStatus(PatientData.State.ACTIVE.name());
        patientEntity.setPatientNumber(patient.getPatientNumber());
        final Patient savedPatient = this.patientRepository.save(patientEntity);
        //save patients contact details
        if (patient.getContactDetails() != null) {
            personContactRepository.saveAll(patient.getContactDetails()
                    .stream()
                    .map(contact -> {
                        final PersonContact contactDetailEntity = ContactData.map(contact);
                        contactDetailEntity.setPerson(savedPatient);
                        return contactDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
        }
        //save patient address details
        if (patient.getAddressDetails() != null) {
            personAddressRepository.saveAll(patient.getAddressDetails()
                    .stream()
                    .map(address -> {
                        final PersonAddress addressDetailEntity = AddressData.map(address);
                        addressDetailEntity.setPerson(savedPatient);
                        return addressDetailEntity;
                    })
                    .collect(Collectors.toList())
            );
        }

        return patient.getPatientNumber();
    }

    public String updatePatient(String patientNumber, Patient patient) {
        try {
            final Patient patientEntity = findPatientOrThrow(patient.getPatientNumber());

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

    private Patient findPatientOrThrow(String patientNumber) {
        return this.patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> APIException.notFound("Patient Number {0} not found.", patientNumber));
    }

    private void throwifDuplicatePatientNumber(String patientNumber) {
        if (patientRepository.existsByPatientNumber(patientNumber)) {
            throw APIException.conflict("Duplicate Patient Number {0}", patientNumber);
        }
    }

}
