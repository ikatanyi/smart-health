package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.inventory.data.InventoryBalanceData;
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
@Table(name = "stock_inventory_balance")
//@IdClass(InventoryId.class)
public class InventoryBalance extends Identifiable {

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

    public static InventoryBalance create(Store store, Item item, double availableStock) {
        return new InventoryBalance(store, item, availableStock);
    }

    public static InventoryBalance create(Store store, Item item) {
        return new InventoryBalance(store, item, 0);
    }

    public InventoryBalanceData toData() {
        InventoryBalanceData data = new InventoryBalanceData();
        data.setId(this.getId());

        if (this.getItem() != null) {
            data.setItem(this.getItem().getItemName());
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
        }

        if (this.getStore() != null) {
            data.setStoreId(this.getStore().getId());
            data.setStoreName(this.getStore().getStoreName());
        }
        data.setAvailableStock(this.getAvailableStock());

        return data;
    }
    
    
}
