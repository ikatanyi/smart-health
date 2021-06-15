package io.smarthealth.integration.data;

import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class MobileMoneyResponseData {
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

    @Enumerated(EnumType.STRING)
    private MobileMoneyProvider provider;
}
