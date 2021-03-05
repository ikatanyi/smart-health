package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.inventory.data.InventoryItemData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
@EqualsAndHashCode(callSuper = false)
//@IdClass(InventoryId.class)
public class InventoryItem extends Identifiable {

//    @Id
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_store_id"))
    private Store store;

//    @Id
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_inventory_item_item_id_"))
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
            if (!this.getItem().getReorderRules().isEmpty()) {
                this.getItem().getReorderRules().stream().filter((rule) -> (rule.getStore() == this.getStore())).forEachOrdered((rule) -> {
                    data.setReorderLevel(rule.getReorderLevel());
                });
                if (data.getReorderLevel() == null) {
                    data.setReorderLevel(this.getItem().getReorderRules().get(0).getReorderLevel());
                }
            }
            data.setDrug(this.getItem().getDrug());
            
        }

        if (this.getStore() != null) {
            data.setStoreId(this.getStore().getId());
            data.setStoreName(this.getStore().getStoreName());
        }
        data.setAvailableStock(this.getAvailableStock());

        return data;
    }
    
     @Override
    public String toString() {
        return "Inventory Item [id=" + getId() + ", item=" +item!=null? item.getItemName() :null+ ", Store=" + store!=null ? store.getStoreName() : null+ ", available stock=" + availableStock + "]";
    }
    
}
