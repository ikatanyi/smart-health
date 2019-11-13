/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.service;

import io.smarthealth.stock.inventory.domain.InventoryItemRepository;
import io.smarthealth.stock.inventory.domain.InventoryVariance;
import io.smarthealth.stock.inventory.domain.InventoryVarianceRepository;
import io.smarthealth.stock.inventory.domain.StockMovementRepository;
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

    public InventoryService(InventoryItemRepository inventoryItemRepository, 
            StockMovementRepository stockMovementRepository, 
            InventoryVarianceRepository inventoryVarianceRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.inventoryVarianceRepository = inventoryVarianceRepository;
    }

    public InventoryVariance saveStockVariance(InventoryVariance variance) {
        return inventoryVarianceRepository.save(variance);
    }
}
