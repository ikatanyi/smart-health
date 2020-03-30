/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PayChannel {

    public enum Type {
        Cash,
        Bank
    }
    //this should be a payment gateway
    private Long accountId; //this can be cash {Petty Cash,Undeposited Funds} Bank {bank account} 
    private String accountNumber;
    private String accountName;
    private Type type;
}
