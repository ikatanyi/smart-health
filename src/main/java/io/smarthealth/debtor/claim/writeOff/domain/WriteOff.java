package io.smarthealth.debtor.claim.writeOff.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name="invoice_write_off")
public class WriteOff extends Auditable{  
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="fk_write_off_id_payer_id")
    private Payer payer;    
    @ManyToOne
    @JoinColumn(name="fk_write_off_id_scheme_id")
    private Scheme scheme;
    private String comments;
    @OneToOne
    @JoinColumn(name="fk_write_off_id_invoice_id")
    private Invoice invoice;
    private Double amount;
}
