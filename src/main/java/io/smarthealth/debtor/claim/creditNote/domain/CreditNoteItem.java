package io.smarthealth.debtor.claim.creditNote.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "patient_credit_note_item")
public class CreditNoteItem extends Auditable {  
    
    @OneToOne
    @JoinColumn(foreignKey=@ForeignKey(name="fk_credit_noteitem_invoice_item"))
    private InvoiceItem invoiceItem;
    private Double amount;
}
