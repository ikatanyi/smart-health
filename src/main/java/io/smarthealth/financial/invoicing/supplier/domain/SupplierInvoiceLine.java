package io.smarthealth.financial.invoicing.supplier.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.product.domain.Product;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_invoice_line")
public class SupplierInvoiceLine extends Identifiable{
    @ManyToOne
    private SupplierInvoice invoice;
      @ManyToOne
    private Product product;
    private double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal tax;
    private BigDecimal discount;
}
