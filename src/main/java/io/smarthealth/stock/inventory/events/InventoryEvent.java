package io.smarthealth.stock.inventory.events;

import io.smarthealth.sequence.UuidGenerator;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author Kelsas
 */ 
@Data
@NoArgsConstructor
public class InventoryEvent implements Serializable {

    public enum Type {
        Adjustment,
        Increase,
        Decrease;
    }
    private String _id = UuidGenerator.newUuid();
    private Type type;
    private Store store;
    private Item item;
    private double quantity;

    public InventoryEvent(Type type, Store storeId, Item itemId, double quantity) {
        this.type = type;
        this.store = storeId;
        this.item = itemId;
        this.quantity = quantity;
    }

}
