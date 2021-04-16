/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.*;
import io.smarthealth.accounting.pettycash.data.enums.PettyCashStatus;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequests;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PettyCashPaymentData {

    private Long id;
    private String payee;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private String voucherNo;
    private String referenceNumber;
    private LocalDate paymentDate;
    private String transactionNo;
    private String requestedBy;
    private LocalDate requestDate;

    public static PettyCashPaymentData of(PaymentData paymentData){
        PettyCashPaymentData data=new PettyCashPaymentData();
        data.setDescription(paymentData.getDescription()!=null ? paymentData.getDescription() : "Other Payments");
        data.setPaymentDate(paymentData.getPaymentDate());
        data.setCredit(paymentData.getAmount());
        data.setDebit(paymentData.getAmount());
        data.setPayee(paymentData.getPayee());
        data.setReferenceNumber(paymentData.getReferenceNumber());
        data.setRequestDate(paymentData.getPaymentDate());
        data.setTransactionNo(paymentData.getTransactionNo());
        data.setVoucherNo(paymentData.getVoucherNo());
        return data;
    }
}
