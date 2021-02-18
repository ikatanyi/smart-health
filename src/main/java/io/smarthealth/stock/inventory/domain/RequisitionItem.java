package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_requisition_item")
public class RequisitionItem extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_requisition_item_requistion_id"))
    private Requisition requistion;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_requisition_item_item_id"))
    private Item item;
    private double price;
    private double quantity;
    private double receivedQuantity;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_requisition_item_uom_id"))
    private Uom uom;

}
