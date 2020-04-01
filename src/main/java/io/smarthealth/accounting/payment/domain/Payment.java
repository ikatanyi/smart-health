/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.PaymentData;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_payments")
public class Payment extends Auditable {

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
//    @OneToMany(mappedBy = "payment")
//    private List<SupplierPayment> invoicePayments = new ArrayList<>();

//    private PaymentInfoCheque paymentInfoCheque;
    public PaymentData toData() {
        PaymentData data = new PaymentData();
        data.setId(this.getId());
        data.setAmount(this.amount);
        data.setPayee(this.payee);
        data.setPayeeId(this.payeeId);
        data.setPayeeType(this.payeeType);
        data.setCurrency(this.currency);
        data.setDescription(this.description);
        data.setPaymentDate(this.paymentDate);
        data.setPaymentMethod(this.paymentMethod);
        data.setReferenceNumber(this.referenceNumber);
        data.setTransactionNo(this.transactionNo);
        data.setVoucherNo(this.voucherNo);
        return data;
    }
}
