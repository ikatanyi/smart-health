/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.security.domain.User;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author simz
 */
@Data
@Entity
@Table(name = "patient_visit_payment_details")
public class PaymentDetails extends Auditable {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_payment_details_visit_id"))
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_payment_details_patient_id"))
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payer_id", foreignKey = @ForeignKey(name = "fk_payment_details_payer"))
    private Payer payer;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
//    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scheme_id", foreignKey = @ForeignKey(name = "fk_payment_details_payee_id"))
    private Scheme scheme;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private String memberName, policyNo, relation, idNo;

    private double limitAmount;
    @Enumerated(EnumType.STRING)
    private CoPayType coPayCalcMethod;
    private double coPayValue;
    private boolean hasCapitation;
    private BigDecimal capitationAmount = BigDecimal.ZERO;
    private double runningLimit;

    @Column(columnDefinition = "tinyint(1) default 1")
    private Boolean limitEnabled = Boolean.TRUE;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean excessAmountEnabled = Boolean.FALSE;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean limitReached = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    private PaymentMethod excessAmountPayMode;//Cash/Credit

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_user_id"))
    @ManyToOne
    private User excessAmountAuthorisedBy;

    private double accumulatedExcessAmount;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_excess_amount_payer_id"))
    @ManyToOne
    private Payer excessAmountPayer;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_payment_details_excess_amount_scheme"))
    @ManyToOne
    private Scheme excessAmountScheme;

    private String authorizationCode;

    private BigDecimal preauthRequestedAmount;
    private BigDecimal preauthApprovedAmount;
    private String preauthCode;
    private LocalDate preauthDate;

    @Override
    public String toString() {
        return "[ runningLimit=" + runningLimit + ", limitAmount=" + limitAmount + "]";
    }
}
