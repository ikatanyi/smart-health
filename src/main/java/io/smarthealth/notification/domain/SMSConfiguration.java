package io.smarthealth.notification.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.notification.domain.enumeration.SMSProvider;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class SMSConfiguration extends Auditable {
    private String apiKey;
    private String senderId;
    private String gatewayUrl;
    private String username;

    @Enumerated(EnumType.STRING)
    private SMSProvider providerName;
    //Active/InActive
    @NotNull
    private String status;


}
