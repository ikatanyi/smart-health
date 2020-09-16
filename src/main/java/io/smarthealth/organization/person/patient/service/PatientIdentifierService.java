/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.organization.person.data.PersonIdentifierData;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientIdentifier;
import io.smarthealth.organization.person.patient.domain.PatientIdentifierRepository;
import java.util.List;
import java.util.Optional;
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

    public PatientIdentifier convertIdentifierDataToEntity(PersonIdentifierData patientIdentifierData) {
        PatientIdentifier patientIdentifier = new PatientIdentifier();
        if (patientIdentifierData.getIdType() == null) {
            return null;
        }
        patientIdentifier.setType(patientIdentificationTypeService.fetchIdType(patientIdentifierData.getIdType()));
        patientIdentifier.setValue(patientIdentifierData.getIdentificationValue());

        return patientIdentifier;
    }

    public PersonIdentifierData convertIdentifierEntityToData(PatientIdentifier patientIdentifier) {
        PersonIdentifierData patientIdentifierData = new PersonIdentifierData();
        patientIdentifierData.setId(patientIdentifier.getId());
        patientIdentifierData.setIdType(patientIdentifier.getType().getId());
        patientIdentifierData.setIdentificationValue(patientIdentifier.getValue());
        patientIdentifierData.setIdentificationType(patientIdentifier.getType().getIdentificationName());
        patientIdentifierData.setValidated(patientIdentifier.getValidated());
        return patientIdentifierData;
    }

    public List<PatientIdentifier> fetchPatientIdentifiers(Patient patient) {
        return patientIdentifierRepository.findByPatient(patient);
    }

    public Optional<PatientIdentifier> fetchPatientIdentifierByPatientAndId(Patient patient, final Long identifierId) {
        return patientIdentifierRepository.findByPatientAndId(patient, identifierId);
    }

}
