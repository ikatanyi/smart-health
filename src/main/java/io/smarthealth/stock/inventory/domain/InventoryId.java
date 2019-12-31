package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.io.Serializable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryId implements Serializable{
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_id_store_id"))
    private Store store;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_id_item_id"))
    private Item item;
}
