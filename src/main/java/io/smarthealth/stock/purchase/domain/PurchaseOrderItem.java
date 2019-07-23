package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
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
@Table(name = "purchase_order_item")
public class PurchaseOrderItem extends Identifiable {

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    private Item item;
    private double quantity;
    private BigDecimal price; //this can be linked to the pricelist and be defined in the values that
    private BigDecimal amount;
}
