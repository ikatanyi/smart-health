package io.smarthealth.financial.invoicing.patient.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.product.domain.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_invoice_line")
public class InvoiceLine extends Auditable {

    public enum Type {
        Item,
        Receipt,
        Discount,
        Interest,
        Tax // like vat
    }

    @Enumerated(EnumType.STRING)
    private Type invoiceLineType;

    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Product product;
    private double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String reference;
}
