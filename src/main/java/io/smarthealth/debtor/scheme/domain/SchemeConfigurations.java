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
import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    private DiscountType discountMethod;
    private double discountValue;
    @Enumerated(EnumType.STRING)
    private CoPayType coPayType;
    private double coPayValue;
    private boolean status;
    private boolean smartEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_scheme_configurations_scheme_id"), unique = true)
    private Scheme scheme;

    private LocalDate copayStartDate;
    private String schemeCover;
    private boolean checkMemberShipLimit;
    private boolean hasClaimSwitching=true;

}
