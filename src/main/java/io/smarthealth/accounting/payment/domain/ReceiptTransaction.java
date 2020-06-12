/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.ReceiptTransactionData;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_receipt_transaction")
public class ReceiptTransaction extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipt_trx_receipt_id"))
    private Receipt receipt;

    private LocalDateTime datetime;
    private String method;
    private BigDecimal amount;
    private String reference;
    @Enumerated(EnumType.STRING)
    private TrnxType type; //Payment Type AUTHORIZE PURCHASE REFUNDS CREDIT
    private String currency;

    public ReceiptTransactionData toData() {
        ReceiptTransactionData data = new ReceiptTransactionData();
        data.setId(this.getId());
        data.setAmount(this.amount);
        data.setCurrency(this.currency);
        data.setDatetime(this.datetime);
        if (this.receipt != null) {
            data.setDescription(this.receipt.getDescription());
            data.setPayer(this.receipt.getPayer());
            data.setReceiptNo(this.receipt.getReceiptNo());
        }
        data.setMethod(this.method);

        data.setReference(this.reference);
        data.setType(this.type);

        return data;
    }
}
