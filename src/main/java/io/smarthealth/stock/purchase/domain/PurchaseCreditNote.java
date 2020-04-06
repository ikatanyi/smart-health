/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.supplier.domain.Supplier;
import java.time.LocalDate;
import java.util.ArrayList;
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
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_credit_note")
public class PurchaseCreditNote extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_credit_note_supplier_id"))
    private Supplier supplier;

    private String creditNoteNumber;
    private String invoiceNumber;
    private LocalDate creditDate;
    @OneToMany(mappedBy = "purchaseCreditNote")
    private List<PurchaseCreditNoteItem> items = new ArrayList<>();
    
      public void addCreditNoteItem(PurchaseCreditNoteItem item) {
        item.setPurchaseCreditNote(this);
        items.add(item);
    }

    public void addCreditNoteItem(List<PurchaseCreditNoteItem> items) {
        this.items = items;
        this.items.forEach(x -> x.setPurchaseCreditNote(this));
    }

}
