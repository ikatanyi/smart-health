package io.smarthealth.administration.mobilemoney.domain;

import io.smarthealth.accounting.accounts.domain.Account;
import io.smarthealth.infrastructure.common.IntegrationStatus;
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

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider mobileMoneyName;

    @Enumerated(EnumType.STRING)
    private BusinessNumberType businessNumberType;

    @Column(unique = true)
    private String businessNumber;
    private String appKey;
    private String appSecret;
    private String passKey;
    private String confirmUrl;
    private String callBackUrl;
    private String validationUrl;

    @Enumerated(EnumType.STRING)
    private IntegrationStatus status;

    //accounting fields
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_mobile_money_integration_account_id"))
    private Account cashAccount;

}
