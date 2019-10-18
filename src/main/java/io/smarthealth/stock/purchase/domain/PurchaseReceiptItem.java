package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
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
@Table(name = "purchase_receipt_item")
public class PurchaseReceiptItem extends Identifiable{

    @ManyToOne
    private PurchaseReceipt purchaseReceipt;
    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipt_item_id"))
    private Item item;
    private double receivedQty;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal discount;
     
}
