/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.domain;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
@Entity
@Table(name = "patient_visit_payment_details")
public class PaymentDetails extends Auditable {

    @ManyToOne
    private Visit visit;

    @ManyToOne
    private Payer payer;

    @ManyToOne
    private Scheme scheme;

    private String policyNo;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private String relation;
    private String memberName;
    private double limitAmount;

}
