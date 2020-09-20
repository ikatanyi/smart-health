/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PrepaymentData {

    private String patientNumber;
    private String patientName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate paymentDate;
    private String paymentMethod;
    private String referenceNo;
    private String memo;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;
    private String shiftNo;
    private String receiptNo;
    private String transactionNo; 
}
