/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.clinical.laboratory.domain.Analyte;
import io.smarthealth.clinical.laboratory.domain.AnalyteRepository;
import io.smarthealth.clinical.laboratory.domain.LabTest;
import io.smarthealth.clinical.laboratory.domain.LabTestRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class AnnalyteService {

    private final AnalyteRepository analyteRepository;
    private final LabTestRepository repository;

    @Transactional
    public List<Analyte> createAnalyte(List<AnalyteData> data) {
        List<Analyte> analytes = new ArrayList<>();

        for (AnalyteData a : data) {
            Analyte analyte = new Analyte();
            //validate lab test
            LabTest labTest = repository.findByCode(a.getTestCode()).orElseThrow(() -> APIException.notFound("Test identified by code {0} not found ", a.getTestCode()));

            analyte.setAnalyte(a.getAnalyte());
            analyte.setUnits(a.getUnits());
            analyte.setLowerLimit(a.getLowerLimit());
            analyte.setUpperLimit(a.getUpperLimit());
            analyte.setReferenceValue(a.getReferenceValue());
            analyte.setDescription(a.getDescription());
            analyte.setLabTest(labTest);
            analytes.add(analyte);
        }
        System.out.println("About to save all analytes " + analytes.size());
        return analyteRepository.saveAll(analytes);

    }
}
