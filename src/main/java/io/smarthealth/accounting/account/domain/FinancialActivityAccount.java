/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_financial_activity_account")
public class FinancialActivityAccount extends Identifiable {
   
    @Column(name = "financial_activity_account_type")
    private Integer activityType;

    @ManyToOne 
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_activity_account_id"))
    private Account account;

    public FinancialActivityAccount() {
    }

    public FinancialActivityAccount(Integer activityType, Account account) {
        this.activityType = activityType;
        this.account = account;
    }
    
}
