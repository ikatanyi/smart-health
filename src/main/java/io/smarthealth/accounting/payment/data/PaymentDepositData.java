/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Kelsas
 */
@Data
public class PaymentDepositData {

    private RecordType type;
    private Long customerId;
    private String customer;
    private String customerNumber;
    private PayerType customerType;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;
    private String reference;
    private String description;
    private BigDecimal amount;
    private BigDecimal balance; 
    private String receiptNo;
    private String transactionNo;

}
