package io.smarthealth.stock.inventory.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.imports.data.InventoryStockData;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.CreateInventoryItem;
import io.smarthealth.stock.inventory.data.ExpiryStock;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.data.ItemDTO;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.inventory.domain.specification.InventoryItemSpecification;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Optional;
import io.smarthealth.stock.inventory.domain.InventoryItemRepository;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Transactional;

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
//    private final ServicePointService servicePointService;
    private final InventoryItemRepository inventoryItemRepository;
    private final StockEntryRepository stockEntryRepository;
    private final SequenceNumberService sequenceNumberService;
    private final InventoryService inventoryService;
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
//        Store store = null;
//        if (storeId != null) {
//            Optional<Store> stor = storeService.getStore(storeId);
//            if (stor.isPresent()) {
//                store = stor.get();
//            }
//        }
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

    @Transactional
    public void processInventoryBalance(InventoryEvent event) {
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

    public void uploadInventoryItems(List<InventoryStockData> itemData) {
        List<InventoryItem> items = new ArrayList<>();
        List<StockEntry> stockEntry = new ArrayList<>();
        int i=0;
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
                    
                    entry.setAmount(BigDecimal.valueOf(x.getStockCount()*NumberUtils.toDouble(item.getRate())));
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

        Map<Store, Double> map = savedStockEntries
                .stream()
                .collect(Collectors.groupingBy(StockEntry::getStore,
                        Collectors.summingDouble(x -> (x.getAmount().doubleValue()))
                        )
                );
        map.forEach((k, v) -> {
        journalService.save(toJournal(k, LocalDate.now(), trdId, BigDecimal.valueOf(v)));
        });
        
    }
    //
     private JournalEntry toJournal(Store store, LocalDate date, String trdId, BigDecimal amount) {
        if (store.getExpenseAccount() == null) {
            throw APIException.notFound("Expense Account is Not Defined for the Store " + store.getStoreName());
        }
//        String debitAcc = store.getExpenseAccount().getIdentifier();
//        String creditAcc = store.getInventoryAccount().getIdentifier();

        String narration = "Opening Balance Inventory for  - " + store.getStoreName();
        JournalEntry toSave = new JournalEntry(date, narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(store.getExpenseAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(store.getInventoryAccount(), narration, BigDecimal.ZERO, amount) 
                }
        );
        toSave.setTransactionNo(trdId);
        toSave.setTransactionType(TransactionType.Balance_Brought_Forward);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }
}
