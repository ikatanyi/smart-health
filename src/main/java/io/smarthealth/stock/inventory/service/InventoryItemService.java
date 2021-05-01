package io.smarthealth.stock.inventory.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.InventoryStockData;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.DateUtility;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.*;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.domain.InventoryItemRepository;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.domain.specification.InventoryItemSpecification;
import io.smarthealth.stock.inventory.events.InventoryCreatedEvent;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final ItemService itemService;
    private final StoreService storeService;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockEntryRepository stockEntryRepository;
    private final SequenceNumberService sequenceNumberService;
    private final JournalService journalService;

    private void decrease(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.decrease(qty);
        inventoryItemRepository.save(balance);
    }

    private void increase(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.increase(qty);
        inventoryItemRepository.save(balance);
    }

    public void updateBalance(Item item, Store store) {
        log.info("Updating Inventory Item Balance");
        Double balance = stockEntryRepository.sumQuantities(item, store);
        InventoryItem inventoryItem = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        inventoryItem.setAvailableStock(balance);
        inventoryItemRepository.save(inventoryItem);
    }

    @Transactional
    public InventoryItem createInventoryItem(InventoryItemData itemData) {
        Item item = itemService.findItemEntityOrThrow(itemData.getItemId());
        Store store = storeService.getStoreWithNoFoundDetection(itemData.getStoreId());
        InventoryItem inventory = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));
        return save(inventory);
    }

    public void createInventoryItem(CreateInventoryItem itemData) {
        List<InventoryItem> items = new ArrayList<>();
        itemData.getInventoryItems()
                .stream()
                .forEach(x -> {
                    Item item = itemService.findItemEntityOrThrow(x.getItemId());
                    Store store = storeService.getStoreWithNoFoundDetection(itemData.getStoreId());
                    InventoryItem inventory = inventoryItemRepository
                            .findByItemAndStore(item, store)
                            .orElse(InventoryItem.create(store, item));
                    if (x.getAvailableStock() > 0) {
                        inventory.setAvailableStock(x.getAvailableStock());
                    }
                    items.add(inventory);
                });
//        return save(inventory);
        inventoryItemRepository.saveAll(items);
    }

    public InventoryItem save(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    private void adjustment(Item item, Store store, double qty) {
        InventoryItem balance = inventoryItemRepository
                .findByItemAndStore(item, store)
                .orElse(InventoryItem.create(store, item));

        balance.setAvailableStock(qty);
        inventoryItemRepository.save(balance);
    }

    public Page<InventoryItem> getInventoryItems(Long storeId, Long itemId, String search, Boolean includeClosed, Pageable page) {
        Specification<InventoryItem> spec = InventoryItemSpecification.createSpecification(storeId, itemId, search, includeClosed);
        Page<InventoryItem> inventoryItems = inventoryItemRepository.findAll(spec, page);

        return inventoryItems;
    }

    public Optional<InventoryItem> getInventoryItem(Long inventoryId) {
        return inventoryItemRepository.findById(inventoryId);
    }

    public Integer getItemCount(String itemCode) {
        Optional<Item> item = itemService.findByItemCode(itemCode);
        if (item.isPresent()) {
            return inventoryItemRepository.findItemCount(item.get());
        } else {
            return 0;
        }
    }

    public Integer getItemCountByItemAndStore(String itemCode, Long storeId) {
        Optional<Item> item = itemService.findByItemCode(itemCode);
        if (item.isPresent()) {
            Store store = storeService.getStoreWithNoFoundDetection(storeId);
            return inventoryItemRepository.findItemCountByItemAndStore(item.get(), store);
        } else {
            return 0;
        }
    }

    public InventoryItem getInventoryItemOrThrow(Long itemId, Long storeId) {
        return getInventoryItem(storeId, itemId)
                .orElseThrow(() -> APIException.notFound("Item with given identifier can not be in given Store"));
    }

    public Optional<InventoryItem> getInventoryItem(Long itemId, Long storeId) {
        Item item = itemService.findItemEntityOrThrow(itemId);
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        return inventoryItemRepository.findByItemAndStore(item, store);
    }

    public List<InventoryItem> getInventoryItemList(Long storeId, List<ItemDTO> items) {
        List<Item> itemlist = items.stream()
                .map(x -> itemService.findItemEntityOrThrow(x.getItemId()))
                .collect(Collectors.toList());

        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        return inventoryItemRepository.findByStoreAndItemIn(store, itemlist);
    }

    public Page<InventoryItemData> getInventoryItemByStore(Long storeId, Pageable page) {
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        return inventoryItemRepository.findByStore(store, page).map(d -> d.toData());
    }

//    @Transactional
//    public void processInventoryBalance(InventoryEvent event) {
//
//        switch (event.getType()) {
//            case Increase:
//                increase(event.getItem(), event.getStore(), event.getQuantity());
//                break;
//            case Decrease:
//                decrease(event.getItem(), event.getStore(), event.getQuantity());
//                break;
//            case Adjustment:
//                adjustment(event.getItem(), event.getStore(), event.getQuantity());
//            default:
//                log.info("Nothing to calculate balance");
//        }
//    }
    public void processInventoryBalance(InventoryCreatedEvent event) {
        switch (event.getType()) {
            case Increase:
                increase(event.getItem(), event.getStore(), event.getQuantity());
                break;
            case Decrease:
                decrease(event.getItem(), event.getStore(), event.getQuantity());
                break;
            case Adjustment:
                adjustment(event.getItem(), event.getStore(), event.getQuantity());
            default:
                log.info("Nothing to calculate balance");
        }
    }

    public List<ExpiryStock> getExpiryStock() {
        return stockEntryRepository.findExpiryStockInterface();
    }

    @Transactional
    public void uploadInventoryItems(List<InventoryStockData> itemData) {
        log.info("START: Upload Inventory items");
        List<InventoryItem> items = new ArrayList<>();
        List<StockEntry> stockEntry = new ArrayList<>();
        int i = 0;
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        itemData.stream()
                .forEach(x -> {
                    Item item = itemService.findByItemCodeOrThrow(x.getItemCode());
                    Store store = storeService.getStoreWithNoFoundDetection(x.getStoreId());
                    InventoryItem inventory = inventoryItemRepository
                            .findByItemAndStore(item, store)
                            .orElse(InventoryItem.create(store, item));
//                    if (x.getStockCount() > 0) {
                    inventory.setAvailableStock(x.getStockCount());
//                    }

                    final String referenceNo = sequenceNumberService.next(1L, Sequences.StockTransferNumber.name());
//                    if(NumberUtils.toDouble(item.getRate())==null){
//                        System.out.println("item.getItemName()");
//                    }
//                    System.out.println(i++ +"========="+item.getItemName());

                    items.add(inventory);
                    StockEntry entry = new StockEntry();

                    entry.setAmount(BigDecimal.valueOf(x.getStockCount() * NumberUtils.toDouble(item.getRate())));
                    entry.setItem(item);
                    entry.setMoveType(MovementType.Opening_Balance);
                    entry.setPrice(item.getRate());
                    entry.setPurpose(MovementPurpose.Receipt);
                    entry.setQuantity(x.getStockCount());
                    entry.setReferenceNumber(referenceNo);
                    entry.setStore(store);
                    entry.setTransactionDate(LocalDate.now());
                    entry.setTransactionNumber(trdId);
                    entry.setUnit(item.getUnit());
                    stockEntry.add(entry);
                    // account posting

                });
//        return save(inventory);
        inventoryItemRepository.saveAll(items);

        List<StockEntry> savedStockEntries = stockEntryRepository.saveAll(stockEntry);
        log.info("END: Upload Inventory items");

        log.info("START: Journal effects");
        Map<Store, Double> map = savedStockEntries
                .stream()
                .collect(Collectors.groupingBy(StockEntry::getStore,
                        Collectors.summingDouble(x -> (x.getQuantity()*x.getPrice().doubleValue()))
                )
                );

        map.forEach((k, v) -> {
            journalService.save(toJournal(k, LocalDate.now(), trdId, BigDecimal.valueOf(v)));
        });

        log.info("END: Journal effects");

    }

    //
    private JournalEntry toJournal(Store store, LocalDate date, String trdId, BigDecimal amount) {
        if (store.getInventoryAccount() == null) {
            throw APIException.notFound("Inventory Account is Not Defined for the Store " + store.getStoreName());
        }
        //get opening balance equity account for openining stocks
        

        String narration = "Opening Balance Inventory for  - " + store.getStoreName();
        JournalEntry toSave = new JournalEntry(
                date,
                narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(store.getInventoryAccount(), narration, amount, BigDecimal.ZERO)
                },
                true
        );
        toSave.setTransactionNo(trdId);
        toSave.setTransactionType(TransactionType.Balance_Brought_Forward);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }


    @Async
    public void doUpdateBalance(Long itemId, Long storeId) {
        Item item = null;
        Store store = null;

        if (itemId != null) {
            item = itemService.findItemEntityOrThrow(itemId);
        }

        if (storeId != null) {
            store = storeService.getStoreWithNoFoundDetection(storeId);
        }

        if (item != null) {
            if (store != null) {
                updateBalance(item, store);
            } else {
                //select all the stores and update the given item
                for (Store stor : storeService.findActiveStores()) {
                    updateBalance(item, stor);
                }
            }
        } else {
            for (InventoryItem invItem : inventoryItemRepository.findAll()) {
                Double balance = stockEntryRepository.sumQuantities(invItem.getItem(), store != null ? store : invItem.getStore());
                invItem.setAvailableStock(balance);
                inventoryItemRepository.save(invItem);
            }
        }

    }

    public List<ItemValuation> getItemValuations(Long storeId, LocalDate date){
        LocalDate asAt = date !=null ? date : LocalDate.now();
        if(storeId!=null){
            return  stockEntryRepository.getItemValuation(storeId,asAt);
        }
        return stockEntryRepository.getItemValuation(asAt);
    }
    public Page<ItemMovement> getItemMovements(DateRange period, Pageable page){
        LocalDate startDate = (period == null ? DateUtility.getStartOfCurrentMonth() : period.getStartDate());
        LocalDate endDate = (period == null ? DateUtility.getEndOfCurrentMonth() : period.getEndDate());
         return stockEntryRepository.getItemMovement(startDate,endDate, page);
    }

}
