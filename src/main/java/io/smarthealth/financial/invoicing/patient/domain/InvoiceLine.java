package io.smarthealth.financial.invoicing.patient.domain;

import io.smarthealth.financial.accounting.domain.Account;
import io.smarthealth.financial.accounting.domain.Period;
import io.smarthealth.common.domain.Identifiable;
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
@Table(name = "patient_invoice_line")
public class InvoiceLine extends Identifiable {

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal grossAmount;
    private BigDecimal vat;
    private BigDecimal discount;
    private BigDecimal netAmount;
    
    /*
    Need a place to hold sales before pushing them to the account
    // description and reference say wh 
    */
}
