/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.infrastructure.domain.Identifiable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity 
public class PettyCashApprovedItems extends Identifiable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_approvals_items_item_no"))
    private PettyCashRequestItems itemNo;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_approvals_items_approval"))
    private PettyCashApprovals approval;

    @Enumerated(EnumType.STRING)
    private PettyCashStatus approvalStatus;

    private String approvalComments;

    private double pricePerUnit;
    private int quantity;
    private double amount;
}
