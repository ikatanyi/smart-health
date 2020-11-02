/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.accounting.payment.data.CopaymentData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.debtor.payer.domain.Scheme;
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
@Table(name = "acc_copayment")
public class Copayment extends Auditable {

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_copay_scheme_id"))
    @ManyToOne
    private Scheme scheme;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_copay_visit_id"))
    private Visit visit;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_copay_invoice_id"))
    private Invoice invoice;

    private LocalDate date;

    private String receiptNumber;

    private BigDecimal amount;
    private Boolean paid;

    public CopaymentData toData() {
        CopaymentData data = new CopaymentData();
        data.setId(this.getId());
        data.setAmount(this.amount);
        data.setDate(this.date);
        if (this.visit != null) {
            data.setPatientName(this.visit.getPatient().getFullName());
            data.setPatientNumber(this.visit.getPatient().getPatientNumber());
            data.setVisitNumber(this.visit.getVisitNumber());
            data.setVisitType(this.visit.getVisitType().name());

        }
        data.setReceiptNumber(this.receiptNumber);

        if (this.scheme != null) {
            data.setScheme(this.scheme.getSchemeName());
            data.setSchemeId(this.scheme.getId());
            data.setPayerId(this.scheme.getPayer().getId());
            data.setPayerName(this.scheme.getPayer().getPayerName());
        }
        data.setPaid(this.paid);
        return data;
    }
}
