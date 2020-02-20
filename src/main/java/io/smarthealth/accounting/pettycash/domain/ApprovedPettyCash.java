/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
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
public class ApprovedPettyCash extends Auditable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_approved_petty_cash_request_no"))
    private PettyCashRequests requestNo;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_approved_petty_cash_item_no"))
    private PettyCashRequestItems itemNo;

    private double pricePerUnit;
    private int quantity;
    private double amount;
}
