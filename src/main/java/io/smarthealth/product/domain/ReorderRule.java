package io.smarthealth.product.domain;

import io.smarthealth.common.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "product_reordering_rule")
public class ReorderRule extends Identifiable {

    @OneToOne(mappedBy = "reorderRule")
    private Product product;

    private String name;
//    name,product,store-store&location, min qty, max qty, qty multiplier, active
    
//    private Store location;
    private Double minQuantity;
    private Double maxQuantity;
    private Double qtyMultiplier;
    private Boolean active;

}
