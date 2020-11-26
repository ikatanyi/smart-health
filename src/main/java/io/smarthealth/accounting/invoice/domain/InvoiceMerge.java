/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Getter
@Setter
@Table(name = "patient_invoice_merged")
public class InvoiceMerge extends Auditable {

    private String fromInvoiceNumber;
    private String toInvoiceNumber;
    private LocalDateTime mergeDatetime;
    private BigDecimal originalInvoiceAmount;
    private BigDecimal newInvoiceAmount;
    private String reasonForMerge;

    public InvoiceMerge() {
    }

    public InvoiceMerge(String fromInvoiceNumber, String toInvoiceNumber, BigDecimal originalInvoiceAmount, BigDecimal newInvoiceAmount, String reasonForMerge) {
        this.fromInvoiceNumber = fromInvoiceNumber;
        this.toInvoiceNumber = toInvoiceNumber;
        this.originalInvoiceAmount = originalInvoiceAmount;
        this.newInvoiceAmount = newInvoiceAmount;
        this.reasonForMerge = reasonForMerge;
        this.mergeDatetime = LocalDateTime.now();
    }

    public InvoiceMerge(Invoice fromInvoiceNumber, Invoice toInvoiceNumber, String reasonForMerge) {
        this.fromInvoiceNumber = fromInvoiceNumber.getNumber();
        this.toInvoiceNumber = toInvoiceNumber.getNumber();
        this.originalInvoiceAmount = toInvoiceNumber.getAmount();
        this.newInvoiceAmount = fromInvoiceNumber.getAmount().add(toInvoiceNumber.getAmount());
        this.reasonForMerge = reasonForMerge;
        this.mergeDatetime = LocalDateTime.now();
    }

}
