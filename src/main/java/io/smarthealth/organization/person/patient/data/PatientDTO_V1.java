/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.data;

import io.smarthealth.organization.person.data.AddressDTO;
import io.smarthealth.organization.person.data.ContactDTO;
import io.smarthealth.organization.person.data.PersonDTO;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientDTO_V1 {

    private String patientNumber;
    private String allergyStatus;
    private String status;
    private String bloodType;
    private boolean isAlive;
    private PersonDTO person;

    public static Patient map(final PatientDTO_V1 patientDto) {
        Patient patientEntity = new Patient();
        patientEntity.setPatientNumber(patientDto.getPatientNumber());
        patientEntity.setAllergyStatus(patientDto.getAllergyStatus());
        patientEntity.setStatus(patientDto.getStatus());
        patientEntity.setBloodType(patientDto.getBloodType());
        patientEntity.setAlive(patientDto.isAlive);
        patientEntity.setDateOfBirth(patientDto.getPerson().getDateOfBirth());
        if (patientDto.getPerson().getAddress() != null) {
            patientEntity.setContacts(patientDto.getPerson().getContact().stream().map(ContactDTO::map).collect(Collectors.toList()));
        }
        if (patientDto.getPerson().getAddress() != null) {
            patientEntity.setAddresses(patientDto.getPerson().getAddress().stream().map(AddressDTO::map).collect(Collectors.toList()));
        }
        patientEntity.setGender(patientDto.getPerson().getGender());
        patientEntity.setGivenName(patientDto.getPerson().getGivenName());
        patientEntity.setMaritalStatus(patientDto.getPerson().getMaritalStatus());
        patientEntity.setMiddleName(patientDto.getPerson().getMiddleName());
        patientEntity.setSurname(patientDto.getPerson().getSurname());
        patientEntity.setTitle(patientDto.getPerson().getTitle());
        return patientEntity;
    }

    public static PatientDTO_V1 map(final Patient patient) {
        PatientDTO_V1 patientDTO = new PatientDTO_V1();
        patientDTO.setPatientNumber(patient.getPatientNumber());
        patientDTO.setAllergyStatus(patient.getAllergyStatus());
        patientDTO.setStatus(patient.getStatus());
        patientDTO.setBloodType(patient.getBloodType());
        patientDTO.setAlive(patient.isAlive());
        patientDTO.getPerson().setDateOfBirth(patient.getDateOfBirth());
        if (patient.getContacts() != null) {
            patientDTO.getPerson().setContact(patient.getContacts().stream().map(ContactDTO::map).collect(Collectors.toList()));
        }
        if (patient.getAddresses() != null) {
            patientDTO.getPerson().setAddress(patient.getAddresses().stream().map(AddressDTO::map).collect(Collectors.toList()));
        }
        patientDTO.getPerson().setGender(patient.getGender());
        patientDTO.getPerson().setGivenName(patient.getGivenName());
        patientDTO.getPerson().setMaritalStatus(patient.getMaritalStatus());
        patientDTO.getPerson().setMiddleName(patient.getMiddleName());
        patientDTO.getPerson().setSurname(patient.getSurname());
        patientDTO.getPerson().setTitle(patient.getTitle());
        return patientDTO;
    }

}
