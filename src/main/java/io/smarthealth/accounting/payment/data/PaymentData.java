/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PaymentData {
    private Long id;
    private Long payeeId;
    private String payee;
    private PayeeType payeeType;
    private LocalDate paymentDate;
    private String voucherNo; //payment number
    private String paymentMethod;
    private BigDecimal amount;
    private String referenceNumber;
    private String description;
    private String transactionNo;
    private String currency;
}
