/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain;

import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
public class PettyCashRequestItems extends Identifiable {

    private String item;
    private double pricePerUnit;
    private int quantity;
    private double amount;
    private String narration;
    private LocalDate requestDate;
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_request_items_request_no"))
    private PettyCashRequests requestNo;
    private Boolean paid;

    //final approval status
    private PettyCashStatus finalApprovalStatus;
    private Double approvedPricePerUnit;
    private Integer approvedQuantity;

}
