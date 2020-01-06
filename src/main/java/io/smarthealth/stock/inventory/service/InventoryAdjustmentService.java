package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.StockAdjustmentData;
import io.smarthealth.stock.inventory.domain.StockAdjustment;
import io.smarthealth.stock.inventory.domain.StockAdjustmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.stock.inventory.domain.specification.StockAdjustmentSpecification;
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
public class InventoryAdjustmentService {

    private final ItemService itemService;
    private final StoreService storeService;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final InventoryEventSender inventoryEventSender;

    // create Invariance
    public StockAdjustment createStockAdjustment(StockAdjustmentData data) {
        StockAdjustment stocks = new StockAdjustment();
        Item item = itemService.findItemEntityOrThrow(data.getItemId());
        Store store = storeService.getStoreWithNoFoundDetection(data.getStoreId());

        stocks.setComments(data.getComments());
        stocks.setDateRecorded(data.getDateRecorded());
        stocks.setItem(item);
        stocks.setStore(store);
        stocks.setQuantity(data.getQuantity());
        stocks.setReasons(data.getReasons());

        StockAdjustment stockAdjstment = stockAdjustmentRepository.save(stocks);

        //TODO : Stock Movement that should affect stock balance
//        trigger a stock movement
        Double qty = stockAdjstment.getQuantity();

        InventoryEvent.Type type = InventoryEvent.Type.Increase;
        if (qty < 0) {
            type = InventoryEvent.Type.Decrease;
        }

        inventoryEventSender.process(new InventoryEvent(type, store.getId(), item.getId(), qty));

        return stockAdjstment;
    }

    public StockAdjustment getStockAdjustment(Long id) {
        return stockAdjustmentRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Stock Adjustment with Id {0} not found", id));
    }

    public Page<StockAdjustmentData> getStockAdjustments(Long storeId, Long itemId, DateRange range, Pageable pageable) {
        Item item = itemService.findItemEntityOrThrow(itemId);
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        Specification<StockAdjustment> spec = StockAdjustmentSpecification.createSpecification(store, item, range);
        Page<StockAdjustmentData> adjstments = stockAdjustmentRepository.findAll(spec, pageable).map(itm -> itm.toData());

        return adjstments;
    }

}
