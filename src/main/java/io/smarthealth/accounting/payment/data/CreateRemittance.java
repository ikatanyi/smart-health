/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Kelsas
 */
@Data
public class CreateRemittance {

    public enum Type {
        Cash,
        Bank
    }

    private Long payerId;
    private String payerName;
    private String payerNumber;
    private BigDecimal amount;
    private LocalDate date;
    private String receivedFrom;

    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;

    private String taxAccountNumber;
    private String referenceNumber;
    private String shiftNo;
    private String currency;
    private String notes;
    private BigDecimal bankCharge;
    private PayChannel paymentChannel;
    private PayerType payerType;
    private RecordType recordType;
    private String visitNumber;
//    private Long typeId; //{0 - Bank ,1 - Petty Cash, 2 Undeposited Fund }
//    private String accountNumber;  //if bank 

    //
}
