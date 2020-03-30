/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.organization.person.patient.data.PatientIdentifierData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import io.smarthealth.organization.person.patient.domain.PatientIdentifierRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class PatientIdentifierService {

    @Autowired
    PatientIdentifierRepository patientIdentifierRepository;

    @Autowired
    PatientIdentificationTypeService patientIdentificationTypeService;

    public PatientIdentifier createPatientIdentifier(PatientIdentifier patientIdentifier) {
        return patientIdentifierRepository.save(patientIdentifier);
    }

    public PatientIdentifier convertIdentifierDataToEntity(PatientIdentifierData patientIdentifierData) {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        if (patientIdentifierData.getId_type().equals("-Select-")) {
            return null;
        }
        patientIdentifier.setType(patientIdentificationTypeService.fetchIdType(Long.valueOf(patientIdentifierData.getId_type())));
        patientIdentifier.setValue(patientIdentifierData.getIdentification_value());

        return patientIdentifier;
    }

    public PatientIdentifierData convertIdentifierEntityToData(PatientIdentifier patientIdentifier) {
        PatientIdentifierData patientIdentifierData = new PatientIdentifierData();
        patientIdentifierData.setId_type(patientIdentifier.getId().toString());
        patientIdentifierData.setIdentification_value(patientIdentifier.getValue());
        patientIdentifierData.setIdentificationType(patientIdentifier.getType().getIdentificationName());
        patientIdentifierData.setValidated(patientIdentifier.getValidated());
        return patientIdentifierData;
    }

    public List<PatientIdentifier> fetchPatientIdentifiers(Patient patient) {
        return patientIdentifierRepository.findByPatient(patient);
    }

}
