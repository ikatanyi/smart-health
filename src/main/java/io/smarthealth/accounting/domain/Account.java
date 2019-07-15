/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Financial Account. Every {@link Account} keeps a actual amount of debit &
 * credit (these are updated with every new {@link Move} related to particular
 * account).
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_account")
public class Account extends Identifiable {

    public enum Type {
        Asset, Liability, Equity, Income, Expense
    }
    private String code;
    @Column(length = 64)
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;
    @ManyToOne
    private Account parent;

}
