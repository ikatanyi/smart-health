/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.data;

import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.domain.enumeration.DiscountType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
public class SchemConfigData {

//    private Long schemeId;
    @Enumerated(EnumType.STRING)
    private DiscountType discountMethod;
    private double discountValue;
    @Enumerated(EnumType.STRING)
    private CoPayType coPayType;
    private double coPayValue;
    private boolean status;
    private boolean smartEnabled;
    private Long configId;

    public static SchemConfigData map(SchemeConfigurations config) {
        SchemConfigData scd = new SchemConfigData();
        scd.setCoPayType(config.getCoPayType());
        scd.setCoPayValue(config.getCoPayValue());
        scd.setDiscountMethod(config.getDiscountMethod());
        scd.setDiscountValue(config.getDiscountValue());
//        scd.setSchemeId(config.getId());
        scd.setSmartEnabled(config.isSmartEnabled());
        scd.setStatus(config.isStatus());
        scd.setConfigId(config.getId());
        return scd;
    }

    public static SchemeConfigurations map(SchemConfigData scheme) {
        SchemeConfigurations sc = new SchemeConfigurations();
        sc.setCoPayType(scheme.getCoPayType());
        sc.setCoPayValue(scheme.getCoPayValue());
        sc.setDiscountMethod(scheme.getDiscountMethod());
        sc.setDiscountValue(scheme.getDiscountValue());
        sc.setSmartEnabled(scheme.isSmartEnabled());
        sc.setStatus(scheme.isStatus());
        return sc;
    }
}
