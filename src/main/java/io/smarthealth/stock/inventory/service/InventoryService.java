package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.domain.StockEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.domain.specification.StockEntrySpecification;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final StockEntryRepository stockEntryRepository;
    private final ItemService itemService;
    private final StoreService storeService;
    private final InventoryEventSender inventoryEventSender;

    public StockEntry createStockEntry(StockEntryData stockData) {

        Item item = itemService.findItemEntityOrThrow(stockData.getItemId());
        Store store = storeService.getStoreWithNoFoundDetection(stockData.getStoreId());

        StockEntry stock = new StockEntry();
        stock.setAmount(stockData.getAmount());
        stock.setDeliveryNumber(stockData.getDeliveryNumber());
        stock.setIssuing(stockData.getIssuing());
        stock.setItem(item);
        stock.setJournalNumber(stockData.getJournalNumber());
        stock.setMoveType(stockData.getMoveType());
        stock.setPrice(stockData.getPrice());
        stock.setPurpose(stockData.getPurpose());
        stock.setReceiving(stockData.getReceiving());
        stock.setReferenceNumber(stockData.getReferenceNumber());
        stock.setStore(store);
        stock.setTransactionDate(stockData.getTransactionDate());
        stock.setTransactionNumber(stockData.getTransactionNumber());
        stock.setUnit(stockData.getUnit());

        StockEntry savedEntry = stockEntryRepository.save(stock);

        //TODO : Stock Movement that should affect stock balance
        Double qty = savedEntry.getReceiving() != null && savedEntry.getReceiving() != 0D ? stock.getReceiving() : stock.getIssuing();

        InventoryEvent.Type type = InventoryEvent.Type.Increase;
        if (savedEntry.getMoveType() == MovementType.Dispensed) {
            type = InventoryEvent.Type.Decrease;
        }

        inventoryEventSender.process(new InventoryEvent(type, store.getId(), item.getId(), qty));

        return stock;
    }

    public StockEntry getStockEntry(Long id) {
        return stockEntryRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Stock Movement with Id {0} not found", id));
    }

    public Page<StockEntry> getStockEntries(String store, String itemName, String referenceNumber, String transactionId, String deliveryNumber, String purpose, String moveType, DateRange range, Pageable pageable) {
        MovementPurpose p = null;
        if (purpose != null) {
            p = MovementPurpose.valueOf(purpose);
        }
        MovementType type = null;
        if (moveType != null) {
            type = MovementType.valueOf(moveType);
        }

        Specification<StockEntry> spec = StockEntrySpecification.createSpecification(store, itemName, referenceNumber, transactionId, deliveryNumber, range, p, type);
        Page<StockEntry> stocks = stockEntryRepository.findAll(spec, pageable);
        return stocks;
    }
//
//    //update stock balances
//    public void updateStockBalance(LocalDateTime date, Item item, Store store, double qty) {
//        InventoryBalance inventory = new InventoryBalance();
//        inventory.setDateRecorded(date);
//        inventory.setItem(item);
//        inventory.setStore(store);
//        inventory.setQuantity(qty);
//        inventoryItemRepository.save(inventory);
//    }
//
//    public void updateStockBalance(InventoryBalanceData data) {
//        InventoryBalance inventory = new InventoryBalance();
//        inventory.setDateRecorded(data.getDateRecorded());
//        Item item = itemService.findItemEntityOrThrow(data.getItemId());
//        Store store = storeService.getStoreWithNoFoundDetection(data.getStoreId());
//        inventory.setItem(item);
//        inventory.setStore(store);
//        inventory.setQuantity(data.getQuantity());
//        inventoryItemRepository.save(inventory);
//    }
//
//    public Page<InventoryBalanceData> getInventoryBalance(Long storeId, Long itemId, DateRange range, Pageable pageable) {
//        Item item = itemService.findItemEntityOrThrow(itemId);
//        Store store = storeService.getStoreWithNoFoundDetection(storeId);
//        Specification<InventoryBalance> spec = InventoryItemSpecification.createSpecification(store, item, range);
//        Page<InventoryBalanceData> inventoryItems = inventoryItemRepository.findAll(spec, pageable).map(itm -> itm.toData());
//
//        return inventoryItems;
//    }
//
//    public Page<InventoryBalanceData> getInventoryBalance(Long itemId, Long storeId, Pageable page) {
//        Item item = itemService.findItemEntityOrThrow(itemId);
//        Store store = null;
//        if (storeId != null) {
//            store = storeService.getStoreWithNoFoundDetection(storeId);
//            return inventoryItemRepository.findByItemAndStore(item, store, page).map(d -> d.toData());
//        }
//        return inventoryItemRepository.findByItem(item, page).map(d -> d.toData());
//    }
//
//    public Page<InventoryBalanceData> getInventoryBalanceByItem(Long itemId, Pageable page) {
//        Item item = itemService.findItemEntityOrThrow(itemId);
//        return inventoryItemRepository.findByItem(item, page).map(d -> d.toData());
//    }
//
//    // create Invariance
//    public StockAdjustment createStockAdjustment(StockAdjustmentData data) {
//        StockAdjustment stocks = new StockAdjustment();
//        Item item = itemService.findItemEntityOrThrow(data.getItemId());
//        Store store = storeService.getStoreWithNoFoundDetection(data.getStoreId());
//
//        stocks.setComments(data.getComments());
//        stocks.setDateRecorded(data.getDateRecorded());
//        stocks.setItem(item);
//        stocks.setStore(store);
//        stocks.setQuantity(data.getQuantity());
//        stocks.setReasons(data.getReasons());
//
//        StockAdjustment stockAdjstment = stockAdjustmentRepository.save(stocks);
//        return stockAdjstment;
//    }
//
//    public StockAdjustment getStockAdjustment(Long id) {
//        return stockAdjustmentRepository.findById(id)
//                .orElseThrow(() -> APIException.notFound("Stock Adjustment with Id {0} not found", id));
//    }
//
//    public Page<StockAdjustmentData> getStockAdjustments(Long storeId, Long itemId, DateRange range, Pageable pageable) {
//        Item item = itemService.findItemEntityOrThrow(itemId);
//        Store store = storeService.getStoreWithNoFoundDetection(storeId);
//        Specification<StockAdjustment> spec = StockAdjustmentSpecification.createSpecification(store, item, range);
//        Page<StockAdjustmentData> adjstments = stockAdjustmentRepository.findAll(spec, pageable).map(itm -> itm.toData());
//
//        return adjstments;
//    }

}
