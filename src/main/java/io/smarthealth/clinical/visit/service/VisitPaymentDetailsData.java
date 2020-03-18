/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.domain.VisitPaymentDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class VisitPaymentDetailsData {

    @ApiModelProperty(hidden = true)
    private Long visitId;
    private String visitNo;
    @ApiModelProperty(hidden = false, required = true)
    private Long payerId, schemeId;
    
    @ApiModelProperty(hidden = true)
    private String patientNo, patientName, payerName, schemeName;
    private String memberName, policyNo, relation, idNo;
    private double limitAmount;

    public static VisitPaymentDetails map(VisitPaymentDetailsData data) {
        VisitPaymentDetails vpd = new VisitPaymentDetails();
        vpd.setIdNo(data.getIdNo());
        vpd.setLimitAmount(data.getLimitAmount());
        vpd.setMemberName(data.getMemberName());
        vpd.setPolicyNo(data.getPolicyNo());
        vpd.setRelation(data.getRelation());
        return vpd;
    }

    public static VisitPaymentDetailsData map(VisitPaymentDetails vpd) {
        VisitPaymentDetailsData data = new VisitPaymentDetailsData();
        data.setIdNo(vpd.getIdNo());
        data.setLimitAmount(vpd.getLimitAmount());
        data.setMemberName(vpd.getMemberName());
        data.setPatientName(vpd.getVisit().getPatient().getFullName());
        data.setPatientNo(vpd.getVisit().getPatient().getPatientNumber());
        data.setPolicyNo(vpd.getPolicyNo());
        data.setRelation(vpd.getRelation());
        data.setVisitId(vpd.getVisit().getId());
        data.setVisitNo(vpd.getVisit().getVisitNumber());
        if(vpd.getPayer()!=null){
            data.setPayerId(vpd.getPayer().getId());
            data.setPayerName(vpd.getPayer().getPayerName());
        }
        if(vpd.getScheme()!=null){
            data.setSchemeId(vpd.getScheme().getId());
            data.setSchemeName(vpd.getScheme().getSchemeName());
        }
        return data;
    }
}
