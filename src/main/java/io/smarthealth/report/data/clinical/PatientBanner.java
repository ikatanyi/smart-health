/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class PatientBanner {

    private String dateOfBirth;
    private String gender;
    private String bloodType;
    private String patientNumber;
    private String address;
    private String allergyStatus;
    private String email;
    @Enumerated(EnumType.STRING)
    private PatientStatus status;
    private Boolean isAlive;
    private String phoneNumber;
    private String fullName;
    private Integer age;

    public static PatientBanner map(PatientData data) {
        PatientBanner patient = new PatientBanner();
        if (data.getAddress() != null && !data.getAddress().isEmpty()) {
            patient.setAddress(data.getAddress().get(0).getPostalCode() + "," + data.getAddress().get(0).getCounty() + "," + data.getAddress().get(0).getCountry() + ", " + data.getAddress().get(0).getTown());
        }
        patient.setAge(data.getAge());
        patient.setAllergyStatus(data.getAllergyStatus());
        patient.setBloodType(data.getBloodType());
        patient.setDateOfBirth(String.valueOf(data.getDateOfBirth()));
        patient.setPhoneNumber(data.getPrimaryContact());
        if (data.getContact() != null && !data.getContact().isEmpty()) {
            patient.setEmail(data.getContact().get(0).getEmail());
           
        }

        patient.setFullName(data.getFullName());
        patient.setGender(data.getGender().name());
        patient.setPatientNumber(data.getPatientNumber());
        patient.setStatus(data.getStatus());
        return patient;
    }
}
