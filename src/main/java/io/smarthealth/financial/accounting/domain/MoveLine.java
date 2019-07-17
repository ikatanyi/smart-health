package io.smarthealth.financial.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import io.smarthealth.product.domain.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "account_move_line")
public class MoveLine extends Identifiable {

    @ManyToOne
    private Move move;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Period period; //
    @ManyToOne
    private Product product; // the service given
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
    private LocalDateTime datePosted;
    private Boolean reconciled;
}
