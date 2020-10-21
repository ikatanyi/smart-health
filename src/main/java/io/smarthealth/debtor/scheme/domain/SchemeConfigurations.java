/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.domain;

import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.domain.enumeration.DiscountType;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author simz
 */
@Entity
@Data
public class SchemeConfigurations extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_scheme_configurations_scheme_id"), unique = true)
    private Scheme scheme;
    @Enumerated(EnumType.STRING)
    private DiscountType discountMethod;
    private double discountValue;

    private boolean smartEnabled;
    private String schemeCover;
    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean checkMemberShipLimit;
    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean claimSwitching;
    
    @Column(name = "has_capitation")
    private boolean capitationEnabled;
    private BigDecimal capitationAmount;
    
    @Column(name = "has_copay")
    private boolean copayEnabled;
    @Enumerated(EnumType.STRING)
    private CoPayType coPayType;
    private double coPayValue;
    private LocalDate copayStartDate;

        private boolean status;
}
