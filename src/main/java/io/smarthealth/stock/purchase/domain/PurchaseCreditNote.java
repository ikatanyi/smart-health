package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "supplier_credit_note")
public class PurchaseCreditNote extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_credit_note_supplier_id"))
    private Supplier supplier;
    private String number;
    private String invoiceNumber;
    private LocalDate creditDate;
    private BigDecimal amount;
    private String reason;
}
