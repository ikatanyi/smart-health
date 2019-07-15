package io.smarthealth.inventory.domain;

import io.smarthealth.common.domain.Auditable;
import io.smarthealth.product.domain.Product;
import io.smarthealth.product.domain.Uom;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_movement")
public class StockMovement extends Auditable {

    public enum Origin {
        // Prescription, Purchases, Stock Inventory - 
        Prescription_Order,
        Purchase_Line,
        Stock_Inventory_Line
    }

    public enum State {
        Draft,
        Assigned,
        Done,
        Cancel
    }

    @Enumerated(EnumType.STRING)
    private Origin origin;
    private Location fromLocation;
    private Location toLocation;
    private Product product;
    private double quantity;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private Uom uom;
    @Enumerated(EnumType.STRING)
    private State state = State.Draft;
    private LocalDateTime effectiveDate;

    //I need a transaction information details here to track the posting.
}
