/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.security.domain.User;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity
@Table(name = "patient_visit_payment_excess_payments")
public class VisitExcessPayments extends Auditable {

    private BigDecimal capitationAmount = BigDecimal.ZERO;

    private String paymode;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_excess_payments_item_id"))
    @ManyToOne
    private Item item;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_excess_payments_user_id"))
    @ManyToOne
    private User excessAmountAuthorisedBy;
}
