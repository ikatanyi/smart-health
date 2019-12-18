/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.inventory.data.InventoryVarianceData;
import io.smarthealth.stock.inventory.domain.InventoryItem;
import io.smarthealth.stock.inventory.domain.InventoryItemRepository;
import io.smarthealth.stock.inventory.domain.InventoryVariance;
import io.smarthealth.stock.inventory.domain.InventoryVarianceRepository;
import io.smarthealth.stock.inventory.domain.StockMovementRepository;
import io.smarthealth.stock.inventory.domain.specification.InventorySpecification;
import io.smarthealth.stock.inventory.domain.specification.VarianceSpecification;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.data.StoreData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InventoryVarianceRepository inventoryVarianceRepository;
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final StoreService storeService;
    public InventoryService(InventoryItemRepository inventoryItemRepository, 
            StockMovementRepository stockMovementRepository, ItemService itemService, StoreService storeService,
            InventoryVarianceRepository inventoryVarianceRepository) {
            this.inventoryItemRepository = inventoryItemRepository;
            this.stockMovementRepository = stockMovementRepository;
            this.inventoryVarianceRepository = inventoryVarianceRepository;
            this.storeService  = storeService;
            this.itemService  = itemService;
    }

    public InventoryVariance saveStockVariance(InventoryVarianceData varianceData) {
        InventoryVariance variance = InventoryVarianceData.map(varianceData);
        InventoryItem inventoryItem = new InventoryItem();
        Item item = itemService.findItemEntityOrThrow(varianceData.getItemId());
        Store store = storeService.getStoreWithNoFoundDetection(varianceData.getStoreId());
        inventoryItem.setItem(item);
        inventoryItem.setStore(store);
        variance.setItem(item);
        variance.setStore(store);
        return inventoryVarianceRepository.save(variance);
    }
    
    public InventoryVariance findOneWithNotFoundDetection(Long id) {
        return inventoryVarianceRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Inventory variance with Id {0} not found", id));
    }
    
     public Page<InventoryVarianceData> getAllStockVariances(LocalDate from, LocalDate to, Long storeId, Pageable pgbl) {
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        Specification<InventoryVariance> spec = VarianceSpecification.createSpecification(from, to, store);
        Page<InventoryVarianceData> inventoryVariances = inventoryVarianceRepository.findAll(spec, pgbl).map(variance -> map(variance));
        return inventoryVariances;
    }
    
     public InventoryItem saveInventoryItem(InventoryItem inventoryItem) {
//        InventoryItem inventoryItem = InventoryItemData.map(itemData);
//        Item item = itemService.findItemEntityOrThrow(itemData.getItemId());
//        Store store = storeService.getStoreWithNoFoundDetection(itemData.getStoreId());
//        inventoryItem.setItem(item);
//        inventoryItem.setStore(store);
        return inventoryItemRepository.save(inventoryItem);
    }
//    
    public InventoryItem findInventoryItemWithNotFoundDetection(Long id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Inventory Item with Id {0} not found", id));
    }
    
    public Optional<InventoryItem> findInventoryItemByItem(Item item) {
        return inventoryItemRepository.findByItem(item);
    }
//    
     public Page<InventoryItemData> getAllInventoryItems(LocalDate from, LocalDate to, String moveType,Long storeId, Pageable pgbl) {
        Store store = storeService.getStoreWithNoFoundDetection(storeId);
        Specification<InventoryItem> spec = InventorySpecification.createSpecification(store, from, to, moveType);
        Page<InventoryItemData> inventoryItems = inventoryItemRepository.findAll(spec,pgbl).map(item -> map(item));
        return inventoryItems;
    }
     
    public InventoryVarianceData map(InventoryVariance variance){
        InventoryVarianceData data = new InventoryVarianceData();
        data.setComments(variance.getComments());
        data.setQuantity(variance.getQuantity());
        data.setReasons(variance.getReasons());
        data.setDateRecorded(LocalDateTime.now());
        if(variance.getStore()!=null)
            data.setStoreData(StoreData.map(variance.getStore()));
        if(variance.getItem()!=null)
             data.setItemData(ItemData.map(variance.getItem()));
        return data;
    }
    
    public InventoryItemData map(InventoryItem inventoryItem){
        InventoryItemData data = new InventoryItemData();
        if(inventoryItem.getStore()!=null)
            data.setStoreData(StoreData.map(inventoryItem.getStore()));
        data.setSerialNumber(inventoryItem.getSerialNumber());
        data.setDateRecorded(LocalDateTime.now());
        data.setId(inventoryItem.getId());
        if(inventoryItem.getItem()!=null)
            data.setItemData(ItemData.map(inventoryItem.getItem()));
        data.setItemType(inventoryItem.getItemType());
        data.setQuantity(inventoryItem.getQuantity());
        data.setSerialNumber(inventoryItem.getSerialNumber());
        data.setStatusType(inventoryItem.getStatusType());
        return data;
    }

    
}
