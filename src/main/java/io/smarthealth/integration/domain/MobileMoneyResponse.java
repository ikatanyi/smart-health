package io.smarthealth.integration.domain;

import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import io.smarthealth.infrastructure.domain.Identifiable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Entity
public class MobileMoneyResponse extends Identifiable {
    private String transactionType;
    private String transID;
    private String transTime;
    private String transAmount;
    private String businessShortCode;
    private String billRefNumber;
    private String invoiceNumber;
    private String orgAccountBalance;
    private String phoneNo;
    private String firstName;
    private String middleName;
    private Boolean patientBillEffected = Boolean.FALSE;

    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider provider;

}
