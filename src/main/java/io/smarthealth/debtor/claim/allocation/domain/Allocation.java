package io.smarthealth.debtor.claim.allocation.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "invoice_allocation")
public class Allocation extends Auditable {  
    @ManyToOne
    private Invoice invoice;
    private Double amount;
    private Double balance;
    private String remittanceNo;
    private String transactionId;
    private String receiptNo;   
}
