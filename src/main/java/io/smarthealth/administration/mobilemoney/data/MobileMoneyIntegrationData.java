package io.smarthealth.administration.mobilemoney.data;

import io.smarthealth.administration.mobilemoney.domain.BusinessNumberType;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import io.smarthealth.infrastructure.common.IntegrationStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class MobileMoneyIntegrationData {
    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider mobileMoneyName;

    @Enumerated(EnumType.STRING)
    private BusinessNumberType businessNumberType;
    private String businessNumber;
    private String appKey;
    private String appSecret;
    private String passKey;
    private String confirmUrl;
    private String callBackUrl;
    private String validationUrl;
    @Enumerated(EnumType.STRING)
    private IntegrationStatus status;

    private Long accountId;

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private String cashAccountName;

    public static MobileMoneyIntegration map(MobileMoneyIntegrationData oi) {
        MobileMoneyIntegration ou = new MobileMoneyIntegration();
        ou.setAppSecret(oi.getAppSecret());
        ou.setAppKey(oi.getAppKey());
        ou.setMobileMoneyName(oi.getMobileMoneyName());
        ou.setBusinessNumber(oi.getBusinessNumber());
        ou.setConfirmUrl(oi.getConfirmUrl());
        ou.setBusinessNumberType(oi.getBusinessNumberType());
        ou.setPassKey(oi.getPassKey());
        ou.setValidationUrl(oi.getValidationUrl());
        ou.setCallBackUrl(oi.getCallBackUrl());
        ou.setStatus(oi.getStatus());

        return ou;
    }

    public static MobileMoneyIntegrationData map(MobileMoneyIntegration oi) {
        MobileMoneyIntegrationData ou = new MobileMoneyIntegrationData();
        ou.setAppSecret(oi.getAppSecret());
        ou.setAppKey(oi.getAppKey());
        ou.setMobileMoneyName(oi.getMobileMoneyName());
        ou.setBusinessNumber(oi.getBusinessNumber());
        ou.setConfirmUrl(oi.getConfirmUrl());
        ou.setBusinessNumberType(oi.getBusinessNumberType());
        ou.setPassKey(oi.getPassKey());
        ou.setValidationUrl(oi.getValidationUrl());
        ou.setCallBackUrl(oi.getCallBackUrl());
        ou.setId(oi.getId());
        ou.setStatus(oi.getStatus());
        if (oi.getCashAccount() != null) {
            ou.setCashAccountName(oi.getCashAccount().getName());
            ou.setAccountId(oi.getCashAccount().getId());
        }
        return ou;
    }
}
