package io.smarthealth.debtor.claim.creditNote.domain;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
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
@Table(name = "credit_note_item")
public class CreditNoteItem extends Identifiable {  
    
    @OneToOne
    @JoinColumn(name="fk_credit_note_item_id_bill_item_id")
    private PatientBillItem billItem;
    @OneToOne
    @JoinColumn(name="fk_credit_note_item_id_item_id")
    private Item item;
    private Double amount;
}
