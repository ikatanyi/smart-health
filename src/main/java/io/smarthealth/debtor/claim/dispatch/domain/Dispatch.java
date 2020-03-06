package io.smarthealth.debtor.claim.dispatch.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name="invoice_dispatch")
public class Dispatch extends Auditable{  
    private String dispatchNo;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name="fk_dispatch_id_payer_id"))
    private Payer payer;
    private LocalDate dispatchDate;
    private String comments;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name="fk_dispatch_id_dispatched_invoice_id"))
    private List<Invoice>dispatchedInvoice;    
}
