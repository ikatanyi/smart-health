/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.person.patient.data.AllergyTypeData;
import io.smarthealth.organization.person.patient.data.PatientAllergiesData;
import io.smarthealth.organization.person.patient.domain.*;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author Simon.Waweru
 */
@Service
@Data
public class AllergiesService {

    private ModelMapper modelMapper;
    private AllergyRepository allergyRepository;
    private AllergyTypeRepository allergyTypeRepository;
    private PatientService patientService;

    public AllergiesService(ModelMapper modelMapper, AllergyRepository allergyRepository, AllergyTypeRepository allergyTypeRepository, PatientService patientService) {
        this.modelMapper = modelMapper;
        this.allergyRepository = allergyRepository;
        this.allergyTypeRepository = allergyTypeRepository;
        this.patientService = patientService;
    }

    @Transactional
    public AllergyType createAllergyType(AllergyType allergyType) {
        return allergyTypeRepository.save(allergyType);
    }

    @Transactional
    public Allergy createPatientAllergy(Allergy allergy) {
        return allergyRepository.save(allergy);
    }

    public List<AllergyType> findAllAllergyTypes() {
        return allergyTypeRepository.findAll();
    }

    public Page<Allergy> fetchPatientAllergies(Patient patient, Pageable pageable) {
        return allergyRepository.findByPatient(patient, pageable);
    }

    public AllergyType findAllergyTypeByCode(String code) {
        return allergyTypeRepository.findByCode(code).orElseThrow(() -> APIException.notFound("Allergy type identified by {0} was not found", code));
    }

    public AllergyType convertAllergyTypeDataToEntity(AllergyTypeData allergyTypeData) {
        AllergyType a = modelMapper.map(allergyTypeData, AllergyType.class);
        return a;
    }

    public AllergyTypeData convertAllergyTypEntityToData(AllergyType allergyType) {
        AllergyTypeData allergyTypeData = modelMapper.map(allergyType, AllergyTypeData.class);
        return allergyTypeData;
    }

    public Allergy convertAllergyDataToEntity(PatientAllergiesData patientAllergiesData) {
        Allergy allergy = modelMapper.map(patientAllergiesData, Allergy.class);
        return allergy;
    }

    public PatientAllergiesData convertPatientAllergiesToData(Allergy allergy) {
        PatientAllergiesData allergiesData = modelMapper.map(allergy, PatientAllergiesData.class);
        if (allergy.getPatient() != null) {
            allergiesData.setPatientData(patientService.convertToPatientData(allergy.getPatient()));
        }
        if (allergy.getAllergyType() != null) {
            allergiesData.setAllergyTypeData(this.convertAllergyTypEntityToData(allergy.getAllergyType()));
        }
        return allergiesData;
    }

}
