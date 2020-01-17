package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.sequence.service.TxnService;
import io.smarthealth.stock.inventory.data.CreateStockEntry;
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
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

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
    private final TxnService txnService;

    @Transactional
    public String createStockEntry(CreateStockEntry stockData) {

        String trxId = txnService.nextId();

        if (!stockData.getItems().isEmpty()) {
            stockData.getItems()
                    .stream()
                    .forEach(st -> {
                        Item item = itemService.findItemEntityOrThrow(st.getItemId());
                        Store store = storeService.getStoreWithNoFoundDetection(stockData.getStoreId());
                        BigDecimal qty = BigDecimal.valueOf(st.getQuantity());
                        Double qtyAmt = stockData.getMovementType() == MovementType.Dispensed ? qty.negate().doubleValue() : qty.doubleValue();

                        StockEntry stock = new StockEntry();
                        stock.setAmount(st.getAmount());
                        stock.setDeliveryNumber(stockData.getDeliveryNumber());
                        stock.setQuantity(qtyAmt);
                        stock.setItem(item);
                        stock.setMoveType(stockData.getMovementType());
                        stock.setPrice(st.getPrice());
                        stock.setPurpose(stockData.getMovementPurpose());
                        stock.setReferenceNumber(stockData.getReferenceNumber());
                        stock.setStore(store);
                        stock.setTransactionDate(stockData.getTransactionDate());
                        stock.setTransactionNumber(trxId);
                        stock.setUnit(st.getUnit());

                        StockEntry savedEntry = stockEntryRepository.save(stock);
                        inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), store, item, qty.doubleValue()));
                    });

        }
        return trxId;
    }

    @Transactional
    public void save(StockEntry entry) {
        StockEntry savedEntry = stockEntryRepository.save(entry);
        inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), savedEntry.getStore(), savedEntry.getItem(), savedEntry.getQuantity()));
    }

    @Transactional
    public void saveAll(List<StockEntry> entry) {
        if (entry.isEmpty()) {
            return;
        }
        entry.stream()
                .forEach(se -> {
                    StockEntry savedEntry = stockEntryRepository.save(se);
                    inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), savedEntry.getStore(), savedEntry.getItem(), savedEntry.getQuantity()));
                }
                );
    }

    private InventoryEvent.Type getEvent(MovementType type) {
        return type == MovementType.Dispensed ? InventoryEvent.Type.Decrease : InventoryEvent.Type.Increase;
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
    //TODO:: Post the changes to the ledgerr
}
