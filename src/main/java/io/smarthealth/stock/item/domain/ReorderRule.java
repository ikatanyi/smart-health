package io.smarthealth.stock.item.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.stores.domain.Store;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
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
public class ReorderRule extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reorder_rule_store_id"))
    private Store store;  
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_reorder_rule_item_id"))
    private Item stockItem;
    
    private double reorderLevel;
    private double reorderQty;
    private boolean active;
}
