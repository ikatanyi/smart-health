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
import javax.persistence.Table; 
 
/**
 *
 * @author Kelsas
 */   
@Entity 
@Table(name = "acc_ledgers")
public class Ledger extends Auditable {

    @Column(name = "a_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private String identifier;
    @Column(name = "a_name")
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_ledgers_parent_ledger_id"))
    private Ledger parentLedger;
    private BigDecimal totalValue;
    private Boolean showAccountsInChart;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Ledger that = (Ledger) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ledger getParentLedger() {
        return parentLedger;
    }

    public void setParentLedger(Ledger parentLedger) {
        this.parentLedger = parentLedger;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Boolean getShowAccountsInChart() {
        return showAccountsInChart;
    }

    public void setShowAccountsInChart(Boolean showAccountsInChart) {
        this.showAccountsInChart = showAccountsInChart;
    }

    @Override
    public String toString() {
        return "Ledger [id=" + getId() + ", name=" + name + ", number=" + identifier + ", type=" + accountType + ", parent ledger=" + parentLedger + " ]";
    }
}
