package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.StockMovementData;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.domain.StockMovement;
import io.smarthealth.stock.inventory.domain.StockMovementRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.domain.enumeration.StatusType;
import io.smarthealth.stock.inventory.domain.specification.StocksSpecification;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.item.service.UomService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.supplier.service.SupplierService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class StockService {

    private final StockMovementRepository stockMovementRepository;
    private final UomService uomService;
    private final StoreService storeService;
    private final ItemService itemService;
    private final InventoryService inventoryService;

    public StockService(StockMovementRepository stockMovementRepository, UomService uomService, StoreService storeService, ItemService itemService, InventoryService inventoryService) {
        this.stockMovementRepository = stockMovementRepository;
        this.uomService = uomService;
        this.storeService = storeService;
        this.itemService = itemService;
        this.inventoryService = inventoryService;
    }

    public Long saveStock(StockMovementData stockData) {
        Double units = 0.0;
        StockMovement stocks = new StockMovement();
        Store store = storeService.getStoreWithNoFoundDetection(stockData.getId());
        stocks.setStore(store);
        Item item = itemService.findItemEntityOrThrow(stockData.getItemId());
        stocks.setItem(item);

        if (stockData.getUomId() != null) {
            Uom uom = uomService.fetchUomById(stockData.getItemId());
            stocks.setUom(uom);
        }

        stocks.setReceiving(stockData.getReceiving());
        stocks.setIssuing(stockData.getIssuing());
        stocks.setUnitPrice(stockData.getUnitPrice());
        stocks.setTotalAmount(stockData.getTotalAmount());
        stocks.setReferenceNumber(stockData.getReferenceNumber());
        stocks.setDeliveryNumber(stockData.getDeliveryNumber());
        stocks.setTransactionNumber(stockData.getTransactionNumber());
        stocks.setJournalNumber(stockData.getJournalNumber());
        stocks.setTransactionDate(stockData.getTransactionDate());
        stocks.setMoveType(stockData.getMoveType());
        stocks.setPurpose(stockData.getPurpose());

        if (stockData.getMoveType().equals("Purchase")) {
            units = +stockData.getReceiving();
        } else {
            units = -stockData.getReceiving();
        }

        InventoryItem inventoryItem = null;
        Optional<InventoryItem> inventory = inventoryService.findInventoryItemByItem(item);
        if (inventory.isPresent()) {
            inventoryItem = inventory.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() + units);
        } else {
            inventoryItem = new InventoryItem();
            inventoryItem.setDateRecorded(LocalDateTime.now());
            inventoryItem.setItem(item);
            inventoryItem.setQuantity(units);
            inventoryItem.setSerialNumber(stockData.getReferenceNumber());
            inventoryItem.setStatusType(StatusType.Good);
            inventoryItem.setStore(store);
        }
        inventoryService.saveInventoryItem(inventoryItem);
        //then we need to save this
        StockMovement savedStock = stockMovementRepository.save(stocks);
        return savedStock.getId();
    }

    public Page<StockMovement> getStockMovements(String store, String itemName, String referenceNumber, String transactionId, String deliveryNumber, String purpose, String moveType, DateRange range, Pageable pageable) {
        MovementPurpose p = null;
        if (purpose != null) {
            p = MovementPurpose.valueOf(purpose);
        }
        MovementType type = null;
        if (moveType != null) {
            type = MovementType.valueOf(moveType);
        }

        Specification<StockMovement> spec = StocksSpecification.createSpecification(store, itemName, referenceNumber, transactionId, deliveryNumber, range, p, type);
        Page<StockMovement> stocks = stockMovementRepository.findAll(spec, pageable);
        return stocks;
    }

    public StockMovement findOneWithNoFoundDetection(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Stock Movement with Id {0} not found", id));
    }

}
