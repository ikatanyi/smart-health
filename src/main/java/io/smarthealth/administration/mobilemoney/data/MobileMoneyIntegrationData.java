package io.smarthealth.administration.mobilemoney.data;

import io.smarthealth.administration.mobilemoney.domain.BusinessNumberType;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyIntegration;
import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
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

    private Long id;

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
        return ou;
    }
}
