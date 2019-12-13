package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.inventory.domain.enumeration.StatusType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Balance Transaction Line of a given {@link Item } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_item")
public class InventoryItem extends Identifiable {
 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_store_id"))
    private Store store;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_item_id"))
    private Item item;
    private double quantity;
    @Enumerated(EnumType.STRING)
    private StatusType statusType;
    private String itemType;
    private String serialNumber;
    private LocalDateTime dateRecorded;

    //
}
