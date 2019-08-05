package io.smarthealth.financial.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *  Value object representing a single monetary transaction towards an account.
 * 
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_transaction_line")
public class TransactionLine extends Identifiable {
 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_trx"))
    private Transaction transaction;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_account_trx_line"))
    private Account account;
    private BigDecimal credit;
    private BigDecimal debit;
    private LocalDateTime transactionDatetime; 
}
