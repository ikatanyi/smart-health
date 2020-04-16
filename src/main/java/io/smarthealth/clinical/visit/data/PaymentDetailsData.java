/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data;

import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author simz
 */
@Data
public class PaymentDetailsData {
///api/integration/smart/claim/{memberNumber}

    private Long visitId;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    private String policyNo;
    private String comments;
    private String relation;
    private String memberName;
    private double limitAmount;
    private Long priceBookId;
    private String priceBookName;
    @Enumerated(EnumType.STRING)
    private CoPayType coPayCalcMethod;
    private double coPayValue;

    public static PaymentDetailsData map(PaymentDetails e) {
        PaymentDetailsData d = new PaymentDetailsData();
        d.setComments(e.getComments());
        d.setMemberName(e.getMemberName());
        d.setPayerId(e.getPayer().getId());
        d.setPolicyNo(e.getPolicyNo());
        d.setRelation(e.getRelation());
        d.setSchemeId(e.getScheme().getId());
        d.setVisitId(e.getVisit().getId());
        d.setLimitAmount(e.getLimitAmount());
        d.setPayerName(e.getPayer().getPayerName());
        d.setSchemeName(e.getScheme().getSchemeName());
        if (e.getCoPayCalcMethod() != null) {
            d.setCoPayCalcMethod(e.getCoPayCalcMethod());
        }
        d.setCoPayValue(e.getCoPayValue());
        if (e.getPayer() != null) {
            if (e.getPayer().getPriceBook() != null) {
                d.setPriceBookId(e.getPayer().getPriceBook().getId());
                d.setPriceBookName(e.getPayer().getPriceBook().getName());
            }
        }
        return d;
    }

    public static PaymentDetails map(PaymentDetailsData d) {
        PaymentDetails e = new PaymentDetails();
        e.setComments(d.getComments());
        e.setMemberName(d.getMemberName());
        e.setPolicyNo(d.getPolicyNo());
        e.setRelation(d.getRelation());
        e.setLimitAmount(d.getLimitAmount());
        return e;
    }
}
