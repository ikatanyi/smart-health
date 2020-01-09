/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.InsuranceScheme;
import io.smarthealth.infrastructure.domain.Auditable;
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
@Table(name = "acc_debtor_invoice")
public class Debtors extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_debtor_invoice_payer_id"))
    private Payer payer;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_debtor_invoice_scheme_id"))
    private Scheme scheme;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_debtor_invoice_invoice_id"))
    private Invoice invoice;
    private String patientNumber;
    private String billNumber;
    private String memberName;
    private String memberNumber;
    private Double invoiceAmount;
    private LocalDate invoiceDate;
    private Double balance;
    private String status;
}
