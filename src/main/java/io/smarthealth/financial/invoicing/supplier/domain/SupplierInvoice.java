package io.smarthealth.financial.invoicing.supplier.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_invoice")
public class SupplierInvoice extends Auditable{
    private String invoiceNumber;
    private String invoiceDate;
    private BigDecimal amount;
     
}
