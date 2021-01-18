package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.AdjustmentData;
import io.smarthealth.stock.inventory.data.StockAdjustmentData;
import io.smarthealth.stock.inventory.domain.StockAdjustment;
import io.smarthealth.stock.inventory.domain.StockAdjustmentRepository;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.inventory.domain.specification.StockAdjustmentSpecification;
import io.smarthealth.stock.inventory.events.InventoryEvent;
import io.smarthealth.stock.inventory.events.InventorySpringEventPublisher;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.stock.inventory.domain.StockEntryRepository;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class InventoryAdjustmentService {

    private final ItemService itemService;
    private final StoreService storeService;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockEntryRepository stockEntryRepository;
//    private final InventoryEventSender inventoryEventSender;
    private final InventorySpringEventPublisher inventoryEventSender;
    private final SequenceNumberService sequenceNumberService;

    // create Invariance
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String createStockAdjustment(AdjustmentData data) {
        String trdId = sequenceNumberService.next(1L, Sequences.Transactions.name());

        if (!data.getAdjustments().isEmpty()) {

            data.getAdjustments()
                    .stream()
                    .forEach(adjt -> {

                        StockAdjustment stocks = new StockAdjustment();
                        Item item = itemService.findItemEntityOrThrow(adjt.getItemId());
                        Store store = storeService.getStoreWithNoFoundDetection(data.getStoreId());

                        Double variance = (adjt.getQuantityCounted() - adjt.getQuantityBalance());
                        adjt.setQuantityAdjusted(variance);

                        stocks.setTransactionId(trdId);
                        stocks.setDateRecorded(data.getDateRecorded());
                        stocks.setItem(item);
                        stocks.setStore(store);
                        stocks.setReasons(adjt.getReasons());
                        stocks.setQuantityBalance(adjt.getQuantityBalance());//computed balance
                        stocks.setQuantityAdjusted(variance);// quantity adjusted
                        stocks.setQuantityCounted(adjt.getQuantityCounted());//saleable

                        StockAdjustment stockAdjstment = save(stocks);

                        if (data.getAdjustmentMode().equals("quantity")) {
//                            inventoryEventSender.process(new InventoryEvent(InventoryEvent.Type.Adjustment, store, item, stockAdjstment.getQuantityCounted()));
                            inventoryEventSender.publishInventoryEvent(InventoryEvent.Type.Adjustment, store, item, stockAdjstment.getQuantityCounted());
                        } 
                    });
        }
        return trdId;
    }

    @Transactional
    public StockAdjustment save(StockAdjustment adjst) {
        StockAdjustment savedAdj = stockAdjustmentRepository.save(adjst);
        StockEntry stock = new StockEntry();
        stock.setAmount(savedAdj.getItem().getRate());
//        stock.setDeliveryNumber("");
        stock.setQuantity(savedAdj.getQuantityAdjusted());
        stock.setItem(savedAdj.getItem());
        stock.setMoveType(MovementType.Stock_Entry);
        stock.setPrice(savedAdj.getItem().getRate());
        stock.setPurpose(MovementPurpose.Adjustment);
        stock.setReferenceNumber(savedAdj.getTransactionId());
        stock.setStore(savedAdj.getStore());
        stock.setTransactionDate(savedAdj.getDateRecorded().toLocalDate());
        stock.setTransactionNumber(savedAdj.getTransactionId());
//        stock.setUnit(st.getUnit());
//        stock.setExpiryDate(st.getExpiryDate());
//        stock.setBatchNo(stock.getBatchNo());

        stockEntryRepository.save(stock);
        return savedAdj;
    }

    public StockAdjustment getStockAdjustment(Long id) {
        return stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Stock Adjustment with Id {0} not found", id));
    }

    public Page<StockAdjustmentData> getStockAdjustments(Long storeId, Long itemId, DateRange range, Pageable pageable) {
        Item item = null;
        Store store = null;
        if (itemId != null) {
            item = itemService.findItemEntityOrThrow(itemId);
        }
        if (storeId != null) {
            store = storeService.getStoreWithNoFoundDetection(storeId);
        }

        Specification<StockAdjustment> spec = StockAdjustmentSpecification.createSpecification(store, item, range);
        Page<StockAdjustmentData> adjstments = stockAdjustmentRepository.findAll(spec, pageable).map(itm -> itm.toData());

        return adjstments;
    } 
}
