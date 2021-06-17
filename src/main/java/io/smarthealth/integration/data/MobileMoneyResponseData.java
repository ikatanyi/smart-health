package io.smarthealth.integration.data;

import io.smarthealth.administration.mobilemoney.domain.MobileMoneyProvider;
import io.smarthealth.integration.domain.MobileMoneyResponse;
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

    public static MobileMoneyResponseData map(MobileMoneyResponse i){
        MobileMoneyResponseData o = new MobileMoneyResponseData();
        o.setBillRefNumber(i.getBillRefNumber());
        o.setFirstName(i.getFirstName());
        o.setBusinessShortCode(i.getBusinessShortCode());
        o.setInvoiceNumber(i.getInvoiceNumber());
        o.setMiddleName(i.getMiddleName());
        o.setFirstName(i.getFirstName());
        o.setPhoneNo(o.getPhoneNo());
        o.setProvider(i.getProvider());
        o.setTransactionType(i.getTransactionType());
        return  o;
    }
}
