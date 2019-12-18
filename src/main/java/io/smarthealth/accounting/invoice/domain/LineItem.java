package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
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
@Data
@Entity 
@Table(name = "invoice_line_item")
public class LineItem extends Identifiable {

    @ManyToOne
     @JoinColumn(name = "invoice_id", foreignKey = @ForeignKey(name = "fk_invoiceline_invoice_id"))
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "item_id", foreignKey = @ForeignKey(name = "fk_invoiceline_item_id"))
    private Item item;
    /**
     * For group line items by type in reporting
     */
    private String type; //indicate the service point -> la
    private String description;
    private Integer quantity;
    private Double unitCost;
    private Double amount;
    private Double discount;
    private Double tax;
    private boolean deleted; 
    //tax
    //discount
}
