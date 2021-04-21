package io.smarthealth.clinical.laboratory.service;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.laboratory.data.LabTestConsumablesData;
import io.smarthealth.clinical.laboratory.domain.*;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.domain.specification.StockEntrySpecification;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.events.InventorySpringEventPublisher;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final LabRegisterTestRepository labRegisterTestRepository;
    private final StoreService storeService;
    private final StockEntryRepository stockEntryRepository;
    private final InventorySpringEventPublisher inventoryEventSender;


    @Transactional
    public List<LabTestConsumables> saveLabTestConsumable(final Long labRegisterTestId, List<LabTestConsumablesData> d) {
        List<LabTestConsumables> consumables = new ArrayList<>();
        //find lab register
        LabRegisterTest labRegisterTest = labRegisterTestRepository.findById(labRegisterTestId).orElseThrow(() -> APIException.notFound("Lab register test identified by id {0} not available ", labRegisterTestId));


        for (LabTestConsumablesData data : d) {
            //find item service
            Item item = itemRepository.findById(data.getConsumableItemId()).orElseThrow(() -> APIException.notFound("Item/Service identified by id {0} not found ", data.getConsumableItemId()));
            Store store = storeService.getStoreWithNoFoundDetection(data.getStoreId());

            LabTestConsumables consumable = new LabTestConsumables();
            consumable.setItem(item);
            consumable.setQuantity(data.getQuantity());
            consumable.setUnitOfMeasure(data.getUnitOfMeasure());
            consumable.setLabRegister(labRegisterTest.getLabRegister());
            consumable.setType(data.getType());
            consumable.setStore(store);

            consumables.add(consumable);
        }

        List<LabTestConsumables> savedConsumables = labTestConsumablesRepository.saveAll(consumables);

        //affect stocks
        List<StockEntry> stockEntries = createStockEntry(savedConsumables);

        stockEntries.stream().forEach((i) -> {
//            doStockEntry(InventoryEvent.Type.Decrease, i, i.getStore(), i.getItem(), i.getQuantity());
            save(i);
        });

//        stockEntryRepository.saveAll(stockEntries);


        //update lab register test
        labRegisterTest.setStockEntryDone(Boolean.TRUE);
        labRegisterTestRepository.save(labRegisterTest);

        return savedConsumables;

    }

    public List<LabTestConsumables> findConsumablesByLabRegister(final Long labRegisterId) {
        LabRegister labRegister = labRegisterRepository.findById(labRegisterId).orElseThrow(() -> APIException.notFound("Consumable identified by id {0} not available ", labRegisterId));

        return labTestConsumablesRepository.findByLabRegister(labRegister);
    }


    private List<StockEntry> createStockEntry(List<LabTestConsumables> labTestConsumables) {
        return labTestConsumables.stream()
                .map(consumable -> {
                    Item item = consumable.getItem();
                    Store store = consumable.getStore();
                    String patientName = "";
                    if (consumable.getLabRegister().getIsWalkin()) {
                        patientName = consumable.getLabRegister().getWalkIn().getFullName();
                    } else {
                        patientName = consumable.getLabRegister().getVisit().getPatient().getFullName();
                    }

                    StockEntry stock = new StockEntry();
                    stock.setAmount(consumable.getItem().getRate().multiply(BigDecimal.valueOf(consumable.getQuantity())));
                    stock.setQuantity(consumable.getQuantity() * -1);
                    stock.setItem(item);
                    stock.setMoveType(MovementType.Dispensed);
                    stock.setPrice(consumable.getItem().getRate());
                    stock.setPurpose(MovementPurpose.Issue);
                    stock.setReferenceNumber(consumable.getLabRegister().getPatientNo());
                    stock.setIssuedTo(consumable.getLabRegister().getPatientNo() + " " + patientName);
                    stock.setStore(store);
                    stock.setTransactionDate(consumable.getLabRegister().getRequestDatetime().toLocalDate());
                    stock.setTransactionNumber(consumable.getLabRegister().getLabNumber());
                    stock.setUnit("");
                    stock.setBatchNo("-");

                    return stock;
                })
                .collect(Collectors.toList());
    }

//    public void doStockEntry(InventoryEvent.Type type, StockEntry stock, Store store, Item item, Double qty) {
//        stockEntryRepository.save(stock);
////        inventoryEventSender.process(new InventoryEvent(type, store, item, qty));
//        inventoryEventSender.publishInventoryEvent(type, store, item, qty);
//    }

    public void save(StockEntry entry) {
        stockEntryRepository.saveAndFlush(entry);
        Double qty = entry.getQuantity();
        if (entry.getPurpose() == MovementPurpose.Issue && entry.getMoveType() == MovementType.Dispensed) {
            if (BigDecimal.valueOf(qty).signum() == -1) {
                qty *= -1;
            }
        }
        inventoryEventSender.publishInventoryEvent(
                getEvent(entry.getMoveType()),
                entry.getStore(),
                entry.getItem(),
                qty
        );
    }

    private InventoryEvent.Type getEvent(MovementType type) {
        return type == MovementType.Dispensed ? InventoryEvent.Type.Decrease : InventoryEvent.Type.Increase;
    }

}
