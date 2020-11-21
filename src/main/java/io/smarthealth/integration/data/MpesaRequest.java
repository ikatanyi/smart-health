/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author Kelsas
 */
@Accessors(fluent = true, chain = true)
@Getter
@Setter
@ToString
public class MpesaRequest {

    String BusinessShortCode;
    String Password;
    String Timestamp;
    String TransactionType;
    String Amount;
    String PartyA;
    String PartyB;
    String PhoneNumber;
    String CallBackURL;
    String AccountReference;
    String TransactionDesc;
}
