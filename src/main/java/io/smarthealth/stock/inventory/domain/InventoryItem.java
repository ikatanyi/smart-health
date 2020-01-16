package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Balance Transaction Line of a given {@link Item } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stock_inventory_item")
//@IdClass(InventoryId.class)
public class InventoryItem extends Identifiable {

//    @Id
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_store_id"))
    private Store store;

//    @Id
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_item_id"))
    private Item item;

    private double availableStock;

    public void decrease(double number) {
        this.availableStock = (this.availableStock - number);
    }

    public void increase(double number) {
        this.availableStock = (this.availableStock + number);
    }

    public static InventoryItem create(Store store, Item item, double availableStock) {
        return new InventoryItem(store, item, availableStock);
    }

    public static InventoryItem create(Store store, Item item) {
        return new InventoryItem(store, item, 0);
    }

    public InventoryItemData toData() {
        InventoryItemData data = new InventoryItemData();
        data.setId(this.getId());

        if (this.getItem() != null) {
            data.setItem(this.getItem().getItemName());
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
            data.setSellingPrice(this.getItem().getRate());
            data.setCostPrice(this.getItem().getCostRate());
        }

        if (this.getStore() != null) {
            data.setStoreId(this.getStore().getId());
            data.setStoreName(this.getStore().getStoreName());
        }
        data.setAvailableStock(this.getAvailableStock());

        return data;
    }
}
