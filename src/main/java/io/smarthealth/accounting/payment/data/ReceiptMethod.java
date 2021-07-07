/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import java.math.BigDecimal;

import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Kelsas
 */
@Data
public class ReceiptMethod {
    private String currency;
    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod method;
    private BigDecimal amount;
    private String reference;

    private String type;
    //what if this was paid from bank I should be able to t
    private String accountNumber;

    private Long referenceAccount; //e.g mobileMoneyId, bankId
}
