package io.smarthealth.notification.data;

import io.smarthealth.notification.domain.SMSConfiguration;
import io.smarthealth.notification.domain.enumeration.SMSProvider;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class SMSConfigurationData {
    private String apiKey;
    private String senderId;
    private String gatewayUrl;
    private String username;
    @Enumerated(EnumType.STRING)
    private SMSProvider providerName;
    private String status;

    private Long id;

    public static SMSConfigurationData map(SMSConfiguration e) {
        SMSConfigurationData data = new SMSConfigurationData();
        data.setUsername(e.getUsername());
        data.setApiKey(e.getApiKey());
        data.setGatewayUrl(e.getGatewayUrl());
        data.setProviderName(e.getProviderName());
        data.setStatus(e.getStatus());
        data.setSenderId(e.getSenderId());
        data.setId(e.getId());
        return data;
    }

    public static SMSConfiguration map(SMSConfigurationData e) {
        SMSConfiguration data = new SMSConfiguration();
        data.setUsername(e.getUsername());
        data.setApiKey(e.getApiKey());
        data.setGatewayUrl(e.getGatewayUrl());
        data.setProviderName(e.getProviderName());
        data.setStatus(e.getStatus());
        data.setSenderId(e.getSenderId());
        return data;

    }
}
