package io.smarthealth.clinical.visit.data;

import io.smarthealth.accounting.billing.domain.enumeration.BillPayMode;
import io.smarthealth.clinical.visit.domain.PaymentDetails;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
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

    @ApiModelProperty(hidden = true)
    private Long visitId;
    private Long payerId;
    @ApiModelProperty(hidden = true)
    private String payerName;
    private Long schemeId;
    @ApiModelProperty(hidden = true)
    private String schemeName;
    private String policyNo;
    private String comments;
    private String relation;
    private String memberName;
    private double limitAmount;
    private Long priceBookId;
    @ApiModelProperty(hidden = true)
    private String priceBookName;
    
    @ApiModelProperty(hidden = true)
    @Enumerated(EnumType.STRING)
    private CoPayType coPayCalcMethod;
    
    @ApiModelProperty(hidden = true)
    private double coPayValue;
    private PaymentMethod paymentMethod;
    private boolean hasCapitation;
    private BigDecimal capitationAmount = BigDecimal.ZERO;
    private double runningLimit;

    @ApiModelProperty(hidden = false)
    private boolean limitEnabled;

    @Enumerated(EnumType.STRING)
    private BillPayMode excessAmountPayMode;

    private Boolean excessAmountEnabled;
    private String idNumber;
    private String authorizationCode;

    public static PaymentDetailsData map(PaymentDetails e) {
        if(e == null ) return null;

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
        d.setPaymentMethod(PaymentMethod.Insurance);
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
        d.setHasCapitation(e.isHasCapitation());
        d.setCapitationAmount(e.getCapitationAmount());
        d.setRunningLimit(e.getRunningLimit());
        d.setLimitEnabled(e.getLimitEnabled());
        d.setIdNumber(e.getIdNo());
        d.setAuthorizationCode(e.getAuthorizationCode());
        return d;
    }

    public static PaymentDetails map(PaymentDetailsData d) {
        PaymentDetails e = new PaymentDetails();
        e.setComments(d.getComments());
        e.setMemberName(d.getMemberName());
        e.setPolicyNo(d.getPolicyNo());
        e.setRelation(d.getRelation());
        e.setLimitAmount(d.getLimitAmount());
        e.setIdNo(d.getIdNumber());
        e.setAuthorizationCode(d.getAuthorizationCode());
        return e;
    }
}
