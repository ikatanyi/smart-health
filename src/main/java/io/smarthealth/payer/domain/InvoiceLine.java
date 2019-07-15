package io.smarthealth.payer.domain;

import io.smarthealth.accounting.domain.Account;
import io.smarthealth.accounting.domain.Period;
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
@Table(name = "invoice_line")
public class InvoiceLine extends Identifiable {

    @ManyToOne
    private Invoice invoice;
    @ManyToOne
    private Product product;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Period period;
    private BigDecimal amount;

}
