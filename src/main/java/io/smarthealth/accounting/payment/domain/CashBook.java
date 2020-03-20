/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.CashBookData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
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
@Table(name = "acc_cash_book")
public class CashBook extends Auditable {

    @Column(name = "transaction_date")
    private LocalDate date;
    private String payee;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal debit;
    private BigDecimal credit;
    private String paymentMode;
    private String referenceNumber; //voucher no,
    private String transactionType;
    private String transactionNo;
    private String currency;

    public CashBookData toData() {
        CashBookData data = new CashBookData();
        data.setId(this.getId());
        data.setPayee(this.payee);
        data.setDescription(this.description);
        data.setDebit(this.debit);
        data.setCredit(this.credit);
        data.setPaymentMode(this.paymentMode);
        data.setReferenceNumber(this.referenceNumber);
        data.setTransactionType(this.transactionType);
        data.setTransactionNo(this.transactionNo);
        data.setCurrency(this.currency);
        return data;
    }
}
