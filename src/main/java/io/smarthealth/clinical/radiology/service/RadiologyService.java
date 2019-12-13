/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.service;

import io.smarthealth.billing.service.PatientBillService;
import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.domain.PatientRadiologyTestRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.service.EmployeeService;
import io.smarthealth.organization.person.patient.service.PatientService;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class RadiologyService {

    RadiologyRepository radiologyRepository;
    PatientRadiologyTestRepository patientradiologyRepository;

    EmployeeService employeeService;
    private PatientService patientservice;
    private PatientBillService billService;
    private ItemService itemService;

    @Transactional
    public List<RadiologyTest> createRadiologyTest(List<RadiologyTestData> radiolgyTestData) {
        try {
            List<RadiologyTest> radiologyTests = radiolgyTestData
                    .stream()
                    .map((radiologyTest) -> {
                        RadiologyTest test = RadiologyTestData.map(radiologyTest);
                        Optional<Item> item = itemService.findByItemCode(radiologyTest.getItemCode());
                        if (item.isPresent()) {
                            test.setItem(item.get());
                        }
                        return test;
                    })
                    .collect(Collectors.toList());
            return radiologyRepository.saveAll(radiologyTests);
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Error occured while creating RadiologyItem ", e.getMessage());
        }
    }

    @Transactional
    public RadiologyTest UpdateRadiologyTest(RadiologyTestData radiolgyTestData) {
        RadiologyTest radiologyTest = this.getById(radiolgyTestData.getId());
        RadiologyTest test = RadiologyTestData.map(radiolgyTestData);
        test.setId(radiologyTest.getId());
        Optional<Item> item = itemService.findByItemCode(radiolgyTestData.getItemCode());
        if (item.isPresent()) {
            test.setItem(item.get());
        }

        return radiologyRepository.save(test);

    }

    @Transactional
    public RadiologyTest getById(Long id) {
        return radiologyRepository.findById(id).orElseThrow(() -> APIException.notFound("Radiology Test identified by {0} not found", id));
    }
    
    @Transactional
    public Page<RadiologyTest> findAll(Pageable pgbl) {
        return radiologyRepository.findAll(pgbl);
    }
}
