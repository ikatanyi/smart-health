package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.accounting.invoice.data.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "acc_misc_invoice_items")
public class MiscellaneousInvoiceItem extends Auditable {

    @ManyToOne
    @JoinColumn(name = "invoice_id", foreignKey = @ForeignKey(name = "fk_misc_inv_invoice_id"))
    private MiscellaneousInvoice invoice;
    private String description;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidedDatetime;
}
