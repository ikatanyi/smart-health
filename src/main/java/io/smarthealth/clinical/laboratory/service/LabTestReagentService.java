package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.LabTestReagentData;
import io.smarthealth.clinical.laboratory.domain.*;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTestReagentService {
    private final LabTestReagentRepository reagentRepository;
    private final ItemRepository itemRepository;
    private final LabEquipmentRepository labEquipmentRepository;
    private final LabTestRepository labTestRepository;

    public LabTestReagent saveNewLabTestReagent(LabTestReagentData data) {
        LabTestReagent reagent = new LabTestReagent();
        //find test
        LabTest labTest = labTestRepository.findById(data.getTestId()).orElseThrow(() -> APIException.notFound("Test identified by id {0} not found ", data.getTestId()));
        //find Equipment
        LabEquipment labEquipment = labEquipmentRepository.findById(data.getEquipmentId()).orElseThrow(() -> APIException.notFound("Equipment identified by id {0} not found ", data.getEquipmentId()));
        //find item service
        Item item = itemRepository.findById(data.getReagentServiceId()).orElseThrow(() -> APIException.notFound("Item/Service identified by id {0} not found ", data.getReagentServiceId()));

        reagent.setReagentService(item);
        reagent.setTest(labTest);
        reagent.setEquipment(labEquipment);

        return reagentRepository.save(reagent);
    }

    public List<LabTestReagent> saveNewLabTestReagents(List<LabTestReagentData> d) {
        List<LabTestReagent> testReagents = new ArrayList<>();

        for (LabTestReagentData data : d) {
            LabTestReagent reagent = new LabTestReagent();
            //find test
            LabTest labTest = labTestRepository.findById(data.getTestId()).orElseThrow(() -> APIException.notFound("Test identified by id {0} not found ", data.getTestId()));
            //find Equipment
            LabEquipment labEquipment = labEquipmentRepository.findById(data.getEquipmentId()).orElseThrow(() -> APIException.notFound("Equipment identified by id {0} not found ", data.getEquipmentId()));
            //find item service
            Item item = itemRepository.findById(data.getReagentServiceId()).orElseThrow(() -> APIException.notFound("Item/Service identified by id {0} not found ", data.getReagentServiceId()));

            reagent.setReagentService(item);
            reagent.setTest(labTest);
            reagent.setEquipment(labEquipment);

            testReagents.add(reagent);
        }

        return reagentRepository.saveAll(testReagents);
    }

    public List<LabTestReagent> fetchByTestAndEquipment(final Long testId, final Long equipmentId) {
        //find test
        LabTest labTest = labTestRepository.findById(testId).orElseThrow(() -> APIException.notFound("Test identified by id {0} not found ", testId));
        //find Equipment
        LabEquipment labEquipment = labEquipmentRepository.findById(equipmentId).orElseThrow(() -> APIException.notFound("Equipment identified by id {0} not found ", equipmentId));

        return  reagentRepository.findByTestAndEquipment(labTest, labEquipment);
    }

}
