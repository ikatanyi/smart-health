/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.service;

import io.smarthealth.stock.inventory.domain.InventoryItemRepository;
import io.smarthealth.stock.inventory.domain.StockMovement;
import io.smarthealth.stock.inventory.domain.StockMovementRepository;
import io.smarthealth.stock.item.domain.Item;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class InventoryService {
    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;

    public InventoryService(InventoryItemRepository inventoryItemRepository, StockMovementRepository stockMovementRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
    }
   
}
