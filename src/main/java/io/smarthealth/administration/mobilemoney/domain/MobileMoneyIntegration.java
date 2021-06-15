package io.smarthealth.administration.mobilemoney.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"mobileMoneyName", "businessNumberType"})
)
public class MobileMoneyIntegration extends Auditable {

    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider mobileMoneyName;

    @Enumerated(EnumType.STRING)
    private BusinessNumberType businessNumberType;

    @Column(unique=true)
    private String businessNumber;
    private String appKey;
    private String appSecret;
    private String passKey;
    private String confirmUrl;
    private String callBackUrl;
    private String validationUrl;

}
