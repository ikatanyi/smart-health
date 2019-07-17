package io.smarthealth.inventory.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.product.domain.Product;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Entity
@Data
public class InventoryVariance extends Auditable {

    private LocalDateTime dateRecorded;
    @OneToOne
    private Location location;
    @ManyToOne
    private Product product;
    private double quantity;
    private String comments;
    @ManyToOne
    @JoinColumn(name = "variance_reason")
    private VarianceReason reasons;
}
