package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.accounting.billing.domain.BillItem;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoice_line_item")
public class InvoiceLineItem extends Auditable {

    @ManyToOne
    @JoinColumn(name = "invoice_id", foreignKey = @ForeignKey(name = "fk_invoiceline_invoice_id"))
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "item_id", foreignKey = @ForeignKey(name = "fk_invoiceline_billitem_id"))
    private BillItem billItem; 
    private boolean deleted;
    private String transactionId;
}
