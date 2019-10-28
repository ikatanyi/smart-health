/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.Disease;
import io.smarthealth.clinical.record.domain.DiseaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    public DiseaseService(DiseaseRepository diseaseRepository) {
        this.diseaseRepository = diseaseRepository;
    }

    public Page<Disease> fetchAllDiseases(Pageable pageable) {
        return diseaseRepository.findAll(pageable);
    }
    public Page<Disease> filterDiseaseByNameOrCode(String name, String code, Pageable pageable) {
        return diseaseRepository.findByNameContainingOrCodeContainingIgnoreCase(name, code, pageable);
    }
}
