/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.domain.PatientIdentificationType;
import io.smarthealth.organization.person.patient.domain.PatientIdentificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class PatientIdentificationTypeService {
    
    @Autowired
    PatientIdentificationTypeRepository identificationTypeRepository;
    
    public PatientIdentificationType creatIdentificationType(PatientIdentificationType patientIdentificationType) {
        try {
            return identificationTypeRepository.save(patientIdentificationType);
        } catch (Exception e) {
            throw APIException.badRequest(e.getMessage(), "");
        }
    }
    
    public List<PatientIdentificationType> fetchAllPatientIdTypes() {
        return identificationTypeRepository.findAll();
    }
    
    public PatientIdentificationType fetchIdType(Long patientIdType) {
        return identificationTypeRepository.findById(patientIdType).get();
    }
}
