package io.smarthealth.debtor.claim.creditNote.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "credit_note")
public class CreditNote extends Auditable {  
    private String creditNoteNo;
    private Double amount;
    private String comments;
    @OneToOne
    @JoinColumn(name="fk_credit_note_id_payer_id")
    private Payer payer;
    @OneToOne
    @JoinColumn(name="fk_credit_note_id_invoice_id")
    private Invoice invoice;
    @OneToMany
    @JoinColumn(name="fk_credit_note_id_credit_note_item_id")
    private List<CreditNoteItem> items;  
}
