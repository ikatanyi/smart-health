package io.smarthealth.stock.inventory.service;

import io.smarthealth.accounting.accounts.domain.JournalEntry;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.accounting.accounts.domain.JournalState;
import io.smarthealth.accounting.accounts.domain.TransactionType;
import io.smarthealth.accounting.accounts.service.JournalService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.CreateStockEntry;
import io.smarthealth.stock.inventory.data.SupplierStockEntry;
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
import io.smarthealth.stock.purchase.service.PurchaseInvoiceService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
    private final PurchaseInvoiceService purchaseInvoiceService;

    private final SequenceNumberService sequenceNumberService;
    private final JournalService journalService;

    @Transactional
    public String createStockEntry(CreateStockEntry stockData) {

        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        final String referenceNo = sequenceNumberService.next(1L, Sequences.StockTransferNumber.name());

        Store store = storeService.getStoreWithNoFoundDetection(stockData.getStoreId());

//        BigDecimal costAmount = BigDecimal.ZERO;
        if (!stockData.getItems().isEmpty()) {
            stockData.getItems()
                    .stream()
                    .forEach(st -> {
                        Item item = itemService.findItemEntityOrThrow(st.getItemId());

                        BigDecimal qty = BigDecimal.valueOf(st.getQuantity());
                        BigDecimal cost = qty.multiply(st.getPrice());

//                        Double qtyAmt = stockData.getMovementPurpose() == MovementPurpose.Issue ? qty.negate().doubleValue() : qty.doubleValue();
//                        Double qtyAmt = qty.negate().doubleValue()
                        StockEntry stock = new StockEntry();
                        stock.setAmount(st.getAmount());
                        stock.setDeliveryNumber(stockData.getDeliveryNumber());
                        stock.setQuantity(qty.negate().doubleValue());
                        stock.setItem(item);
                        stock.setMoveType(stockData.getMovementType());
                        stock.setPrice(st.getPrice());
                        stock.setPurpose(stockData.getMovementPurpose());
                        stock.setReferenceNumber(referenceNo);
                        stock.setStore(store);
                        stock.setTransactionDate(stockData.getTransactionDate());
                        stock.setTransactionNumber(trdId);
                        stock.setUnit(st.getUnit());

//                        stockEntryRepository.save(stock);
//                        inventoryEventSender.process(new InventoryEvent(InventoryEvent.Type.Decrease, store, item, qty.doubleValue()));
                        doStockEntry(InventoryEvent.Type.Decrease, stock, store, item, qty.doubleValue());

                        if (stockData.getMovementPurpose() == MovementPurpose.Transfer) {
                            Store destinationStore = storeService.getStoreWithNoFoundDetection(stockData.getDestinationStoreId());
                            StockEntry receivingStock = new StockEntry();
                            receivingStock.setAmount(st.getAmount());
                            receivingStock.setDeliveryNumber(stockData.getDeliveryNumber());
                            receivingStock.setItem(item);
                            receivingStock.setMoveType(stockData.getMovementType());
                            receivingStock.setPrice(st.getPrice());
                            receivingStock.setPurpose(MovementPurpose.Receipt);
                            receivingStock.setQuantity(qty.doubleValue());
                            receivingStock.setStore(destinationStore);
                            receivingStock.setReferenceNumber(referenceNo);
                            receivingStock.setTransactionDate(stockData.getTransactionDate());
                            receivingStock.setTransactionNumber(trdId);
                            receivingStock.setUnit(st.getUnit());
                            doStockEntry(InventoryEvent.Type.Increase, receivingStock, destinationStore, item, qty.doubleValue());
                        }

                    });

        }

        if (stockData.getMovementPurpose() == MovementPurpose.Issue) {
            BigDecimal costAmount = stockData.getItems()
                    .stream()
                    .map(x -> (x.getPrice().multiply(BigDecimal.valueOf(x.getQuantity()))))
                            .reduce(BigDecimal.ZERO, (x,y) -> (x.add(y)));

            journalService.save(toJournal(store, stockData.getTransactionDate(), trdId, costAmount));
        }
        return trdId;
    }

    private void doStockEntry(InventoryEvent.Type type, StockEntry stock, Store store, Item item, Double qty) {
        stockEntryRepository.save(stock);
        inventoryEventSender.process(new InventoryEvent(type, store, item, qty));
    }

    private JournalEntry toJournal(Store store, LocalDate date, String trdId, BigDecimal amount) {
        if (store.getExpenseAccount() == null) {
            throw APIException.notFound("Expense Account is Not Defined for the Store " + store.getStoreName());
        }
        String debitAcc = store.getExpenseAccount().getIdentifier();
        String creditAcc = store.getInventoryAccount().getIdentifier();

        String narration = "Issuing Stocks for  - " + store.getStoreName();
        JournalEntry toSave = new JournalEntry(date, narration,
                new JournalEntryItem[]{
                    new JournalEntryItem(store.getExpenseAccount(), narration, amount, BigDecimal.ZERO),
                    new JournalEntryItem(store.getInventoryAccount(), narration, BigDecimal.ZERO, amount)
//                    new JournalEntryItem(narration, debitAcc, JournalEntryItem.Type.DEBIT, amount),
//                    new JournalEntryItem(narration, creditAcc, JournalEntryItem.Type.CREDIT, amount)
                }
        );
        toSave.setTransactionNo(trdId);
        toSave.setTransactionType(TransactionType.Stock_Issuing);
        toSave.setStatus(JournalState.PENDING);
        return toSave;
    }

    @Transactional
    public String receiveSupplierStocks(SupplierStockEntry stockData) {
        // we will do stock entry and then create a bill out of this for easy 
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());
        stockData.setTransactionId(trdId);
        String dnote = stockData.getSupplierInvoiceNumber() != null ? stockData.getSupplierInvoiceNumber() : trdId;
        Store store = storeService.getStoreWithNoFoundDetection(stockData.getStoreId());
        if (!stockData.getItems().isEmpty()) {
            stockData.getItems()
                    .stream()
                    .forEach(st -> {
                        Item item = itemService.findItemEntityOrThrow(st.getItemId());

                        BigDecimal qty = BigDecimal.valueOf(st.getQuantity());

                        StockEntry stock = new StockEntry();
                        stock.setAmount(st.getAmount());
                        stock.setDeliveryNumber(dnote);
                        stock.setQuantity(qty.doubleValue());
                        stock.setItem(item);
                        stock.setMoveType(MovementType.Purchase);
                        stock.setPrice(st.getPrice());
                        stock.setPurpose(MovementPurpose.Receipt);
                        stock.setReferenceNumber(stockData.getSupplierInvoiceNumber());
                        stock.setStore(store);
                        stock.setTransactionDate(stockData.getTransactionDate());
                        stock.setTransactionNumber(trdId);
                        stock.setUnit(st.getUnit());

                        StockEntry savedEntry = stockEntryRepository.save(stock);
                        inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), store, item, qty.doubleValue()));
                    });

        }

        purchaseInvoiceService.createPurchaseInvoice(store, stockData);

        return trdId;
    }

    //create supplier invoice
    public void save(StockEntry entry) {
        stockEntryRepository.saveAndFlush(entry);

        inventoryEventSender.process(
                new InventoryEvent(
                        getEvent(entry.getMoveType()),
                        entry.getStore(),
                        entry.getItem(),
                        entry.getQuantity())
        );
    }

    @Transactional
    public void saveAll(List<StockEntry> entry) {
        if (entry.isEmpty()) {
            return;
        }
        List<StockEntry> savedList = stockEntryRepository.saveAll(entry);
        stockEntryRepository.flush();

        if (!savedList.isEmpty()) {
            savedList
                    .forEach((savedEntry) -> {
                        inventoryEventSender.process(new InventoryEvent(getEvent(savedEntry.getMoveType()), savedEntry.getStore(), savedEntry.getItem(), savedEntry.getQuantity()));
                    });
        }
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
