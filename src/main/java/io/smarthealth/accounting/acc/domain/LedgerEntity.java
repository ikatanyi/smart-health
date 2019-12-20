package io.smarthealth.accounting.acc.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal; 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table; 
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */  
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "acc_ledgers")
public class LedgerEntity extends Auditable {

    @Column(name = "a_type")
    private String type;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "a_name")
    private String name;
    @Column(name = "description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ledger_id", foreignKey = @ForeignKey(name = "fk_ledgers_parent_ledger_id"))
    private LedgerEntity parentLedger;
    @Column(name = "total_value")
    private BigDecimal totalValue;
    @Column(name = "show_accounts_in_chart")
    private Boolean showAccountsInChart;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LedgerEntity that = (LedgerEntity) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

}
