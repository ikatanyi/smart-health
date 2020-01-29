/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.member.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author simz
 */
@Table(
        uniqueConstraints
        = @UniqueConstraint(columnNames = {"scheme_id", "policyNo"})
)
@Entity
@Data
public class PayerMember extends Auditable {

    @ManyToOne
    private Scheme scheme;

    @Column(nullable = false)
    private String policyNo;

    private String idNo;
    private String contactNo;
    @Column(nullable = false)
    private String memberName;
    private String relation;
    private LocalDate dob;
    private boolean status;
    private double limitAmount;
}
