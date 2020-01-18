package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.stock.item.domain.Item;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "pricebook_items")
public class PriceItem implements Serializable {

    @EmbeddedId
    private PriceItemId id;

    @ManyToOne
    @MapsId("pricebook_id") //This is the name of attr in EmployerDeliveryAgentPK class
    @JoinColumn(name = "pricebook_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pricebook_items_pricebook_id"))
    public PriceBook pricebookId;
       
    @ManyToOne
    @MapsId("item_id") //This is the name of attr in EmployerDeliveryAgentPK class
    @JoinColumn(name = "item_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pricebook_items_pricebook_item_id"))
    private Item itemId;
      
    private Double amount; 

    public PriceItem() {
    }

    public PriceItem(PriceItemId id, Double amount) {
        this.id = id;
        this.amount = amount;
    }
}
