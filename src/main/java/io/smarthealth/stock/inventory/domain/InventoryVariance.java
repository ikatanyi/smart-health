package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
    private Department store;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_variance_item_id"))
    private Item item;
    private double quantity;
    private String comments;
    @ManyToOne
    @JoinColumn(name = "variance_reason")
    private VarianceReason reasons;
}
