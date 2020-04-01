/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.old.domain;

import io.smarthealth.accounting.old.data.PaymentoldData;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
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
@Table(name = "payments")@Deprecated
public class Paymentold extends Auditable {

    @ManyToOne
    private FinancialTransaction transaction;
    
    private String method;

    private Double amount;

    private String referenceCode;

    private String type;

    private String currency;
    
     public  PaymentoldData toData() {
        return new PaymentoldData(
                this.getId(),
                this.method,
                this.amount,
                this.referenceCode,
                this.type,
                this.currency
        );
    }
}
