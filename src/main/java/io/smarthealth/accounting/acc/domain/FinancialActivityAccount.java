 package io.smarthealth.accounting.acc.domain;

import io.smarthealth.accounting.acc.data.v1.FinancialActivity;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Kelsas
 */
 
 @Data
 @EqualsAndHashCode(callSuper = false)
@Entity 
@Table(name = "acc_financial_activity")
public class FinancialActivityAccount extends Identifiable {

    @Enumerated(EnumType.STRING)
    private FinancialActivity financialActivity;

    @ManyToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_financial_activity_account_id"))
    private AccountEntity account;

    public FinancialActivityAccount() {
    }

    public FinancialActivityAccount(FinancialActivity financialActivity, AccountEntity account) {
        this.financialActivity = financialActivity;
        this.account = account;
    }

}
