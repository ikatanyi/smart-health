/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.events;

import java.util.List;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class InventoryCreatedEvent extends ApplicationEvent {

    private final List<InventoryEvent> inventoryEvent;

    public InventoryCreatedEvent(Object source, List<InventoryEvent> inventoryEvent) {
        super(source);
        this.inventoryEvent = inventoryEvent;
    }

    public List<InventoryEvent> getInventoryEvent() {
        return inventoryEvent;
    }
 

}
