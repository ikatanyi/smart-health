/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.medicaltemplate.service;

import io.smarthealth.administration.medicaltemplate.data.MedicalTemplateData;
import io.smarthealth.administration.medicaltemplate.domain.MedicalTemplate;
import io.smarthealth.administration.medicaltemplate.domain.MedicalTemplateRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class MedicalTemplateService {
    
    private final MedicalTemplateRepository medicalTemplateRepository;
    
    public MedicalTemplate saveMedicalTemplate(MedicalTemplateData templateData) {
//if exists drop
        Optional<MedicalTemplate> temp = medicalTemplateRepository.findByTemplateName(templateData.getTemplateName());
        
        if (temp.isPresent()) {
            medicalTemplateRepository.delete(temp.get());
        }
        
        return medicalTemplateRepository.save(templateData.fromData());
    }
    
    public MedicalTemplate fetchMedicalTemplateByName(String templateName) {
        return medicalTemplateRepository.findByTemplateName(templateName).orElseThrow(() -> APIException.notFound("Medical template identified by {0} not found", templateName));
    }
}
