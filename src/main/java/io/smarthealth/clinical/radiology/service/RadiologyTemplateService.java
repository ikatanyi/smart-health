/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.service;

import io.smarthealth.clinical.radiology.domain.RadiologyTemplate;
import io.smarthealth.clinical.radiology.domain.RadiologyTemplateNotes;
import io.smarthealth.clinical.radiology.domain.Repository.RadiologyTemplateNotesRepository;
import io.smarthealth.clinical.radiology.domain.Repository.RadiologyTemplateRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class RadiologyTemplateService {

    @Autowired
    RadiologyTemplateRepository radiologyTemplateRepository;

    @Autowired
    RadiologyTemplateNotesRepository radiologyTemplateNotesRepository;

    @Transactional
    public RadiologyTemplate createRadiologyTemplate(final RadiologyTemplate template) {
        return radiologyTemplateRepository.save(template);
    }

    @Transactional
    public List<RadiologyTemplateNotes> createRadiologyTemplateNotes(List<RadiologyTemplateNotes> itrbl) {
        return radiologyTemplateNotesRepository.saveAll(itrbl);
    }
}
