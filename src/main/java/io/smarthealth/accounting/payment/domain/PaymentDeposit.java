/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.data.PaymentDepositData;
import io.smarthealth.accounting.payment.domain.enumeration.PayerType;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.RecordType;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "acc_payment_deposits")
public class PaymentDeposit extends Auditable {

    @Enumerated(EnumType.STRING)
    private RecordType type;
    @Enumerated(EnumType.STRING)
    private PayerType customerType;
    private Long customerId;
    private String customer;
    private String customerNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;

    private String reference;
    private String description;
    private BigDecimal amount;
    private BigDecimal balance; 
   
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_deposit_receipt_id"))
    private Receipt receipt;
    private String transactionNo;

    public PaymentDepositData toData() {
        PaymentDepositData data = new PaymentDepositData();
        data.setType(this.type);
        data.setCustomerType(this.customerType);
        data.setCustomerId(this.customerId);
        data.setCustomer(this.customer);
        data.setCustomerNumber(this.customerNumber);
        data.setPaymentDate(this.paymentDate);
        data.setPaymentMethod(this.paymentMethod);
        data.setReference(this.reference);
        data.setDescription(this.description);
        data.setAmount(this.amount);
        data.setBalance(this.balance); 
        data.setReceiptNo(this.receipt!=null ?  this.receipt.getReceiptNo() : null);
        data.setTransactionNo(this.transactionNo);

        return data;
    }
}
