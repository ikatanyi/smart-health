package io.smarthealth.accounting.accounts.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@Table(name = "acc_accounts")
public class Account extends Auditable {

    @Column(name = "a_type")
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "a_name")
    private String name;
    @Column(name = "balance")
    private BigDecimal balance;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_account_id", foreignKey = @ForeignKey(name = "fk_account_reference_account_id"))
    private Account referenceAccount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //ManyToOne
    @JoinColumn(name = "ledger_id", foreignKey = @ForeignKey(name = "fk_account_ledger_id"))
    private Ledger ledger;
    @Column(name = "a_state")
    @Enumerated(EnumType.STRING)
    private AccountState state;
    
     @Override
    public String toString() {
        return "Account [id=" + getId() + ", name=" + name + ", number=" + identifier + ", type=" + type + ", ledger=" +ledger + " ]";
    }
}
