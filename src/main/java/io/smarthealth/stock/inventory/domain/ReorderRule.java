package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.company.facility.domain.Department; 
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_reorder_rule")
public class ReorderRule extends Identifiable{
    private Department store;
    /** When this quantity is reached, the reorder will be triggered */
    private double reorderLevel;
    private double reorderQty;
    @ManyToOne
    private Item stockItem;
}
