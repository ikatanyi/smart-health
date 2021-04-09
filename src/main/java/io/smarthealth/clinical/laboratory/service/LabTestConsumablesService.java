package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.laboratory.data.LabTestConsumablesData;
import io.smarthealth.clinical.laboratory.domain.LabRegister;
import io.smarthealth.clinical.laboratory.domain.LabRegisterRepository;
import io.smarthealth.clinical.laboratory.domain.LabTestConsumables;
import io.smarthealth.clinical.laboratory.domain.LabTestConsumablesRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabTestConsumablesService {

    private final LabTestConsumablesRepository labTestConsumablesRepository;
    private final ItemRepository itemRepository;
    private final LabRegisterRepository labRegisterRepository;
    private final StoreService storeService;

    public List<LabTestConsumables> saveLabTestConsumable(final Long labRegisterId, List<LabTestConsumablesData> d) {
        List<LabTestConsumables> consumables = new ArrayList<>();
        //find lab register
        LabRegister labRegister = labRegisterRepository.findById(labRegisterId).orElseThrow(() -> APIException.notFound("Consumable identified by id {0} not available ", labRegisterId));


        for (LabTestConsumablesData data : d) {
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

        List<LabTestConsumables> savedConsumables = labTestConsumablesRepository.saveAll(consumables);
        //affect stocks
        //this fn has been halted
       //List<StockEntry> stockEntries = createStockEntry(savedConsumables);

        return savedConsumables;

    }

    public List<LabTestConsumables> findConsumablesByLabRegister(final Long labRegisterId) {
        LabRegister labRegister = labRegisterRepository.findById(labRegisterId).orElseThrow(() -> APIException.notFound("Consumable identified by id {0} not available ", labRegisterId));

        return labTestConsumablesRepository.findByLabRegister(labRegister);
    }

/*
    private List<StockEntry> createStockEntry(List<LabTestConsumables>  labTestConsumables) {
        return labTestConsumables.stream()
                .filter(x -> x.getStoreId() != null)
                .map(consumable -> {
                    Item item = drug.getItem();
                    Store store = storeService.getStoreWithNoFoundDetection(consumable.getStoreId());

                    BigDecimal amt = BigDecimal.valueOf(consumable.getAmount());
                    BigDecimal price = BigDecimal.valueOf(consumable.getPrice());

                    StockEntry stock = new StockEntry();
                    stock.setAmount(amt);
                    stock.setQuantity(consumable.getQuantity() * -1);
                    stock.setItem(item);
                    stock.setMoveType(MovementType.Dispensed);
                    stock.setPrice(price);
                    stock.setPurpose(MovementPurpose.Issue);
                    stock.setReferenceNumber(patientBill.getPatient().getPatientNumber());
                    stock.setIssuedTo(patientBill.getPatient().getPatientNumber() + " " + patientBill.getPatient().getFullName());
                    stock.setStore(store);
                    stock.setTransactionDate(consumable.getBillingDate());
                    stock.setTransactionNumber(consumable.getTransactionId());
                    stock.setUnit("");
                    stock.setBatchNo("-");

                    return stock;
                })
                .collect(Collectors.toList());
    }*/

}
