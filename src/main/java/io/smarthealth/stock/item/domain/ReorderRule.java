package io.smarthealth.stock.item.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.stores.domain.Store;
import javax.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_reorder_rule")
public class ReorderRule extends Identifiable {

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reorder_rule_store_id"))
    private Store store;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reorder_rule_item_id"))
    private Item stockItem;

    private double reorderLevel;
    private double reorderQty;
    private boolean active;

    @Override
    public String toString() {
        return "ReorderRule(" + this.getStore().getStoreName() + ", " + this.getStockItem().getItemName() + ", " + this.getReorderLevel() + ", " + this.getReorderQty() + ")";
    }
}
