package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_transaction_line")
public class TransactionLine extends Identifiable {

    public enum Type {
        Sale,
        Purchase,
        Receipt
    }
//    TransactionType (can be sale, purchase, receipt etc.)
    @Enumerated(EnumType.STRING)
    private Type transactionType;
    @OneToOne
    private FiscalYear period;
    @OneToOne
    private Account account;
    @ManyToOne
    private Transaction transaction;
    
    private String reference;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
    private LocalDateTime datePosted;
    private Boolean reconciled;
    
}
