package io.smarthealth.stock.item.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "stock_drugs")
public class Drug extends Identifiable{
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_stock_drugs_item_id"))
    private Item item;
    private String drugCategory;
    private String strength;
    private String route;
    private String drugForm;
    
}
