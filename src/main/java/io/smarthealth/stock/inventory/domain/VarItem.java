package io.smarthealth.stock.inventory.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Column;
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
@Table(name = "variance_item")
public class VarItem extends Identifiable{  
   @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_var_item_item_id"))    
    private Item item;       
    @Column(name = "variance_reason")
    private String reason;
    private Integer quantity;
    private Integer variance;
}
