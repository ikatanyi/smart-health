package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.clinical.laboratory.data.LabTestConsumablesData;
import io.smarthealth.clinical.laboratory.domain.LabRegister;
import io.smarthealth.clinical.laboratory.domain.LabRegisterRepository;
import io.smarthealth.clinical.laboratory.domain.LabTestConsumables;
import io.smarthealth.clinical.laboratory.domain.LabTestConsumablesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabTestConsumablesService {
    private final LabTestConsumablesRepository labTestConsumablesRepository;
    private final ItemRepository itemRepository;
    private final LabRegisterRepository labRegisterRepository;

    public List<LabTestConsumables> saveLabTestConsumable(final Long labRegisterId, List<LabTestConsumablesData> d) {
        List<LabTestConsumables> consumables = new ArrayList<>();
        //find lab register
        LabRegister labRegister = labRegisterRepository.findById(labRegisterId).orElseThrow(() -> APIException.notFound("Consumable identified by id {0} not available ", labRegisterId));


        for (LabTestConsumablesData data: d) {
            //find item service
            Item item = itemRepository.findById(data.getConsumableItemId()).orElseThrow(() -> APIException.notFound("Item/Service identified by id {0} not found ", data.getConsumableItemId()));
            LabTestConsumables consumable = new LabTestConsumables();
            consumable.setItem(item);
            consumable.setQuantity(data.getQuantity());
            consumable.setUnitOfMeasure(data.getUnitOfMeasure());
            consumable.setLabRegister(labRegister);
            consumable.setType(data.getType());
            consumables.add(consumable);
        }

        return labTestConsumablesRepository.saveAll(consumables);
    }

    public List<LabTestConsumables> findConsumablesByLabRegister(final Long labRegisterId) {
        LabRegister labRegister = labRegisterRepository.findById(labRegisterId).orElseThrow(() -> APIException.notFound("Consumable identified by id {0} not available ", labRegisterId));

        return labTestConsumablesRepository.findByLabRegister(labRegister);
    }

}
