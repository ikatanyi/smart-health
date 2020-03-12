/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.service;

import io.smarthealth.clinical.radiology.data.RadiologyTestData;
import io.smarthealth.clinical.radiology.data.ServiceTemplateData;
import io.smarthealth.clinical.radiology.domain.RadiologyRepository;
import io.smarthealth.clinical.radiology.domain.RadiologyTest;
import io.smarthealth.clinical.radiology.domain.ServiceTemplate;
import io.smarthealth.clinical.radiology.domain.ServiceTemplateRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
@RequiredArgsConstructor
public class RadiologyConfigService {

    private final ServiceTemplateRepository serviceTemplateRepository;
    
    private final RadiologyRepository radiologyRepository;
    
    private final ItemService itemService;

    @Transactional
    public List<ServiceTemplate> createServiceTemplate(List<ServiceTemplateData> serviceTemplateData) {
        List<ServiceTemplate> serviceTemplates = serviceTemplateData
                .stream()
                .map((x) -> ServiceTemplateData.map(x))
                .collect(Collectors.toList());
        return serviceTemplateRepository.saveAll(serviceTemplates);

    }

    @Transactional
    public ServiceTemplate UpdateServiceTemplate(Long id, ServiceTemplateData serviceTemplateData) {
        ServiceTemplate serviceTemplate = getServiceTemplateByIdWithFailDetection(id);
        serviceTemplate.setGender(serviceTemplateData.getGender());
        serviceTemplate.setNotes(serviceTemplateData.getNotes());
        serviceTemplate.setTemplateName(serviceTemplateData.getTemplateName());        

         return serviceTemplateRepository.save(serviceTemplate);

    }


    public ServiceTemplate getServiceTemplateByIdWithFailDetection(Long id) {
        return serviceTemplateRepository.findById(id).orElseThrow(() -> APIException.notFound("ServiceTemplate identified by id {0} not found ", id));
    }
    
    public Optional<ServiceTemplate> getServiceTemplateById(Long id) {
        return serviceTemplateRepository.findById(id);
    }

    @Transactional
    public Page<ServiceTemplate> findAllTemplates(Pageable pgbl) {
        return serviceTemplateRepository.findAll(pgbl);
    }
    
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
                        Optional<ServiceTemplate> template = this.getServiceTemplateById(radiologyTest.getTemplateId());
                        if(template.isPresent())
                            test.setServiceTemplate(template.get());
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

    public RadiologyTest findScanByItem(final Item item) {
        return radiologyRepository.findByItem(item).orElseThrow(() -> {
            return APIException.notFound("Radiology Test not Registered in Radiology Department");
        });
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
