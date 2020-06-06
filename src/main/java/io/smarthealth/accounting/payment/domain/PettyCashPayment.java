/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.PettyCashPaymentData;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_pettycash_payments")
public class PettyCashPayment extends Auditable {

    private String payee;
    private String description;
    private BigDecimal debit;
    private BigDecimal credit;
    private String voucherNo;
    private LocalDate paymentDate;
    private String transactionNo;
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_pay_id"))
    @ManyToOne
    private Payment payment;
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_petty_cash_request_id"))
    @ManyToOne
    private PettyCashRequestItems pettyCashRequest;
    
    public PettyCashPaymentData toData(){
        PettyCashPaymentData data=new PettyCashPaymentData();
        data.setId(this.getId());
        data.setCredit(this.credit);
        data.setDebit(this.debit);
        data.setDescription(this.description);
        data.setPayee(this.payee);
        data.setPaymentDate(this.paymentDate);
        data.setReferenceNumber(this.payment.getReferenceNumber());
        data.setVoucherNo(this.payment.getVoucherNo());
        if(this.pettyCashRequest!=null){
          data.setRequestedBy(this.pettyCashRequest.getRequestNo().getRequestedBy().getFullName());
        }
        return data;
    }
}
