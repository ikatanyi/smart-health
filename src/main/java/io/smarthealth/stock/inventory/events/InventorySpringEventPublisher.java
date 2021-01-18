/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.events;

import io.smarthealth.stock.inventory.events.InventoryEvent.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;

/**
 *
 * @author Kelsas
 */
@lombok.extern.slf4j.Slf4j
@Component
public class InventorySpringEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishInventoryEvent(Type type, Store storeId, Item itemId, double quantity) {
        log.info("Publishing Inventory Event... {} ", quantity);
        InventoryCreatedEvent inventoryEvent = new InventoryCreatedEvent(this, type, storeId, itemId, quantity);
        applicationEventPublisher.publishEvent(inventoryEvent);
    }
}
