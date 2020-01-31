package io.smarthealth.stock.purchase.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_order_item")
public class PurchaseOrderItem extends Identifiable {
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_item_order_id"))
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purch_order_item_item_id"))
    private Item item;
    
    private double quantity;
    private double receivedQuantity;
    private BigDecimal price; //this can be linked to the pricelist and be defined in the values that
    private BigDecimal amount;
    
}
