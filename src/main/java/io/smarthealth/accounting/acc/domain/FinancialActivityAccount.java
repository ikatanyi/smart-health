/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.acc.domain;

import io.smarthealth.accounting.acc.data.v1.FinancialActivity;
import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
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
