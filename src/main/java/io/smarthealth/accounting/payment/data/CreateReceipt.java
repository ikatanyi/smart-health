/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateReceipt {

    public enum Type {
        Deposit,
        Payment
    }

    public enum CustomerType {
        Patient,
        Insurance,
        Others
    }
    private Type type;

    private Long customerId;
    private String customer;
    private String customerNumber;
    private CustomerType customerType;

    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate paymentDate;
    private String paymentMethod;
    private String reference;
    private String description;
    private BigDecimal amount;
    private String currency;
    private String shiftNo;
    private BigDecimal bankCharge;
    private String taxAccount;
    private String taxAccountNumber;
    private PayChannel depositedTo;  //this should be a chart of account
}
