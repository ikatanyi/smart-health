/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.data;

import io.smarthealth.debtor.scheme.domain.SchemeConfigurations;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.debtor.scheme.domain.enumeration.DiscountType;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
public class SchemConfigData {

    private DiscountType discountMethod;
    private double discountValue;

    private boolean copayEnabled;
    private CoPayType coPayType;
    private double coPayValue;
    private LocalDate copayStartDate;

    private boolean status;
    private boolean smartEnabled;
    private Long configId;

    private String schemeCover;
    private boolean checkMemberShipLimit;
    private boolean hasClaimSwithing;

    private boolean capitationEnabled;
    private BigDecimal capitationAmount;

    @ApiModelProperty(hidden = true)
    private String schemeName;
    @ApiModelProperty(hidden = true)
    private String payerName;

    public static SchemConfigData map(SchemeConfigurations config) {
        SchemConfigData scd = new SchemConfigData();

        scd.setConfigId(config.getId());

        scd.setCopayEnabled(config.isCopayEnabled());
        scd.setCoPayType(config.getCoPayType());
        scd.setCoPayValue(config.getCoPayValue());
        scd.setCopayStartDate(config.getCopayStartDate());

        scd.setDiscountMethod(config.getDiscountMethod());
        scd.setDiscountValue(config.getDiscountValue());
        scd.setStatus(config.isStatus());
//        scd.setSchemeId(config.getId());
        scd.setSmartEnabled(config.isSmartEnabled());

        scd.setSchemeCover(config.getSchemeCover());
        scd.setCheckMemberShipLimit(config.isCheckMemberShipLimit());
        scd.setHasClaimSwithing(config.isClaimSwitching());

        scd.setCapitationAmount(config.getCapitationAmount());
        scd.setCapitationEnabled(config.isCapitationEnabled());

        if (config.getScheme() != null) {
            scd.setSchemeName(config.getScheme().getSchemeName());
            if (config.getScheme().getPayer() != null) {
                scd.setPayerName(config.getScheme().getPayer().getPayerName());
            }
        }

        return scd;
    }

    public static SchemeConfigurations map(SchemConfigData schemeData) {
        SchemeConfigurations sc = new SchemeConfigurations();
        sc.setCopayEnabled(schemeData.isCopayEnabled());
        sc.setCoPayType(schemeData.getCoPayType());
        sc.setCoPayValue(schemeData.getCoPayValue());
        sc.setDiscountMethod(schemeData.getDiscountMethod());
        sc.setDiscountValue(schemeData.getDiscountValue());
        sc.setSmartEnabled(schemeData.isSmartEnabled());
        sc.setStatus(schemeData.isStatus());

        sc.setSchemeCover(schemeData.getSchemeCover());
        sc.setCheckMemberShipLimit(schemeData.isCheckMemberShipLimit());
//        sc.setHasClaimSwitching(schemeData.isHasClaimSwithing());

        sc.setCapitationEnabled(schemeData.isCapitationEnabled());
        sc.setCapitationAmount(schemeData.getCapitationAmount());

        return sc;
    }
}
