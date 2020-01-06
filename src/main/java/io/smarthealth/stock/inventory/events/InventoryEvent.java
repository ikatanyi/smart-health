package io.smarthealth.stock.inventory.events;

import io.smarthealth.infrastructure.utility.UuidGenerator;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@NoArgsConstructor
public class InventoryEvent implements Serializable {

    public enum Type {
        Increase,
        Decrease;
    }
    private String _id = UuidGenerator.newUuid();
    private Type type;
    private Long storeId;
    private Long itemId;
    private double quantity;

    public InventoryEvent(Type type, Long storeId, Long itemId, double quantity) {
        this.type = type;
        this.storeId = storeId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

}
