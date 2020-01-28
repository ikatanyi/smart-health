/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data;

import io.smarthealth.clinical.visit.domain.PaymentDetails;
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
    private Long schemeId;
    private String policyNo;
    private String comments;
    private String relation;
    private String memberName;

    public static PaymentDetailsData map(PaymentDetails e) {
        PaymentDetailsData d = new PaymentDetailsData();
        d.setComments(e.getComments());
        d.setMemberName(e.getMemberName());
        d.setPayerId(e.getPayer().getId());
        d.setPolicyNo(e.getPolicyNo());
        d.setRelation(e.getRelation());
        d.setSchemeId(e.getScheme().getId());
        d.setVisitId(e.getVisit().getId());
        return d;
    }

    public static PaymentDetails map(PaymentDetailsData d) {
        PaymentDetails e = new PaymentDetails();
        e.setComments(d.getComments());
        e.setMemberName(d.getMemberName());
        e.setPolicyNo(d.getPolicyNo());
        e.setRelation(d.getRelation());
        return e;
    }
}
