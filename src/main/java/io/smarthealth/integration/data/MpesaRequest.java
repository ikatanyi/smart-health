package io.smarthealth.integration.data;

import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */ 
@ToString
public class MpesaRequest {

   private String BusinessShortCode;
   private  String Password;
   private  String Timestamp;
   private  String TransactionType;
   private  String Amount;
   private  String PartyA;
   private  String PartyB;
   private  String PhoneNumber;
   private  String CallBackURL;
   private  String AccountReference;
   private  String TransactionDesc;

    public MpesaRequest() {
    }

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(String BusinessShortCode) {
        this.BusinessShortCode = BusinessShortCode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String Timestamp) {
        this.Timestamp = Timestamp;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String TransactionType) {
        this.TransactionType = TransactionType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getPartyA() {
        return PartyA;
    }

    public void setPartyA(String PartyA) {
        this.PartyA = PartyA;
    }

    public String getPartyB() {
        return PartyB;
    }

    public void setPartyB(String PartyB) {
        this.PartyB = PartyB;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getCallBackURL() {
        return CallBackURL;
    }

    public void setCallBackURL(String CallBackURL) {
        this.CallBackURL = CallBackURL;
    }

    public String getAccountReference() {
        return AccountReference;
    }

    public void setAccountReference(String AccountReference) {
        this.AccountReference = AccountReference;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String TransactionDesc) {
        this.TransactionDesc = TransactionDesc;
    }
    
}
