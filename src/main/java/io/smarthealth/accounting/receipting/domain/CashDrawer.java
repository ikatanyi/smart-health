/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.receipting.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "acc_cash_drawer")
public class CashDrawer extends Identifiable {

    @Column(name = "drawer_name")
    private String name;
    private String tenderTypes;
    @Column(length = 25)
    private String receiptAccount;
    @Column(length = 25)
    private String expenseAccount;
    @Column(length = 25)
    private String openingCashAccount;
    private boolean active;
}
