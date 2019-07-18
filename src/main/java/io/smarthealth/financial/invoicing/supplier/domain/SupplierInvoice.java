package io.smarthealth.financial.invoicing.supplier.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.partner.supplier.domain.Supplier;
import io.smarthealth.purchase.domain.PurchaseOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_invoice")
public class SupplierInvoice extends Auditable {

    public enum Status {
        Draft,
        Reconcilled,
        Approved,
        Paid
    }
    @ManyToOne
    private Supplier supplier;
    private String reference;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "invoice")
    private List<SupplierInvoiceLine> invoiceLines;
    
    private PurchaseOrder order;
    //TO link to PurchaseOrder
}
