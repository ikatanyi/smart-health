/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.events;

import io.smarthealth.stock.inventory.events.InventoryEvent.Type;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Kelsas
 */
public class InventoryCreatedEvent extends ApplicationEvent {

//    public enum Type {
//        Adjustment,
//        Increase,
//        Decrease;
//    }

    private final Type type;
    private final Store store;
    private final Item item;
    private final double quantity;

    public InventoryCreatedEvent(Object source, Type type, Store storeId, Item itemId, double quantity) {
        super(source);

        this.type = type;
        this.store = storeId;
        this.item = itemId;
        this.quantity = quantity;
    }

    public Type getType() {
        return type;
    }

    public Store getStore() {
        return store;
    }

    public Item getItem() {
        return item;
    }

    public double getQuantity() {
        return quantity;
    }

}
