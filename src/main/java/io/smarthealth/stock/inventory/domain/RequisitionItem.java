package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
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
@Table(name = "stock_requisition_item")
public class RequisitionItem extends Identifiable {

    @ManyToOne
    private Requisition requistion;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_requistion_item_id"))
    private Item item;
    private double quantity;
    @ManyToOne
    private Uom uom;

}
