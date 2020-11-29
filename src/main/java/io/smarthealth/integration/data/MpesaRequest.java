package io.smarthealth.integration.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter 
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
    
}
