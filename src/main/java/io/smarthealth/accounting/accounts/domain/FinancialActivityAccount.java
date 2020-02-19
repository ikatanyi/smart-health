package io.smarthealth.accounting.accounts.domain;
  
import io.smarthealth.accounting.accounts.data.FinancialActivity;
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
    private Account account;

    public FinancialActivityAccount() {
    }

    public FinancialActivityAccount(FinancialActivity financialActivity, Account account) {
        this.financialActivity = financialActivity;
        this.account = account;
    }

}
