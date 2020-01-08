package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.data.StockAdjustmentData;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

/**
 * Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_adjustment")
public class StockAdjustment extends Auditable {

    private LocalDateTime dateRecorded;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_store_id"))
    private Store store;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_item_id"))
    private Item item;
    private Double quantityBalance;
    private Double quantityCounted;
    private double quantityAdjusted;
    private String transactionId;

    @Column(name = "variance_reason")
    private String reasons;
    //use code - Stock Variance Reason

    public StockAdjustmentData toData() {
        StockAdjustmentData data = new StockAdjustmentData();
        data.setId(this.getId());
        if (this.getStore() != null) {
            data.setStoreId(this.getStore().getId());
            data.setStoreName(this.getStore().getStoreName());
        }
        if (this.getItem() != null) {
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
            data.setItem(this.getItem().getItemName());
        }

        data.setDateRecorded(this.getDateRecorded());
        
        data.setQuantityAdjusted(this.getQuantityAdjusted());
        data.setQuantityBalance(this.getQuantityBalance());
        data.setQuantityCounted(this.getQuantityCounted());
        
        data.setTransactionId(this.getTransactionId());
        data.setReasons(this.getReasons());
        return data;
    }
}
