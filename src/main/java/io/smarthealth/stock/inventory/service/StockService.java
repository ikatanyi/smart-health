package io.smarthealth.stock.inventory.service;

import io.smarthealth.accounting.pricebook.service.PricebookService;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.StockMovementData;
import io.smarthealth.stock.inventory.domain.StockMovement;
import io.smarthealth.stock.inventory.domain.StockMovementRepository;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.inventory.domain.specification.StocksSpecification;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.item.service.UomService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import io.smarthealth.supplier.service.SupplierService;
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

    public StockService(StockMovementRepository stockMovementRepository, UomService uomService, StoreService storeService, ItemService itemService) {
        this.stockMovementRepository = stockMovementRepository;
        this.uomService = uomService;
        this.storeService = storeService;
        this.itemService = itemService;
    }

    public Long saveStock(StockMovementData stockData) {
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

        //then we need to save this
        StockMovement savedStock = stockMovementRepository.save(stocks);
        return savedStock.getId();
    }

    public Page<StockMovement> getStockMovements(String store, String itemName, String referenceNumber, String transactionId, String deliveryNumber, String purpose, String moveType, DateRange range, Pageable pageable) {
        MovementPurpose p=null;
        if(purpose!=null){
            p=MovementPurpose.valueOf(purpose);
        }
        MovementType type=null;
        if(moveType!=null){
            type=MovementType.valueOf(moveType);
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
