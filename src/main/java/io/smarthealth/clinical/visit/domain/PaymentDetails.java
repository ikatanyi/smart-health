/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 *
 * @author simz
 */
@Data
@Entity
@Table(name = "patient_visit_payment_details")
public class PaymentDetails extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

}
