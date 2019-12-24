package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_inventory_variance")
public class InventoryVariance extends Auditable {

    private LocalDateTime dateRecorded;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_store_id"))
    private Store store;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_variance_item_id"))
    private Item item;
    private double quantity;
    private String comments; 
    @Column(name = "variance_reason")
    private String reasons;
    //use code - Stock Variance Reason
}
