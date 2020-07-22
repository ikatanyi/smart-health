package io.smarthealth.debtor.claim.creditNote.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.accounting.invoice.domain.Invoice;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteData;
import io.smarthealth.debtor.claim.creditNote.data.CreditNoteItemData;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "patient_credit_note")
public class CreditNote extends Auditable {

    private String creditNoteNo;
    private Double amount;
    private String comments;
    private String transactionId;
    private LocalDate creditDate;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_credit_note_id_payer_id"))
    private Payer payer;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_credit_note_id_invoice_id"))
    @ManyToOne
    private Invoice invoice;

    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_credit_note_id_credit_note_credit_note_item_id"))
    private List<CreditNoteItem> items;

    public CreditNoteData toData() {
        CreditNoteData data = new CreditNoteData();
        data.setId(this.getId());
        data.setAmount(this.getAmount());
        data.setCreditNoteNo(this.getCreditNoteNo());

        List items = this.getItems().stream().map((item) -> {
            CreditNoteItemData creditNoteItemData = new CreditNoteItemData();
            creditNoteItemData.setId(item.getId());
            creditNoteItemData.setAmount(item.getInvoiceItem().getBillItem().getAmount());
            creditNoteItemData.setItemName(item.getInvoiceItem().getBillItem().getItem().getItemName());
            creditNoteItemData.setInvoiceItemId(item.getInvoiceItem().getId());
            creditNoteItemData.setItemId(item.getInvoiceItem().getBillItem().getItem().getId());
            creditNoteItemData.setQuantity(item.getInvoiceItem().getBillItem().getQuantity());
            creditNoteItemData.setItemCode(item.getInvoiceItem().getBillItem().getItem().getItemCode());
            creditNoteItemData.setUnitPrice(item.getInvoiceItem().getBillItem().getPrice());
            return creditNoteItemData;
        }).collect(Collectors.toList());

        data.setCreditNoteItems(items);
        if (this.getInvoice() != null) {
            data.setInvoiceNo(this.getInvoice().getNumber());
            data.setInvoiceDate(this.getInvoice().getDate());
        }
        if (this.getPayer() != null) {
            data.setPayer(this.getPayer().getPayerName());
            data.setPayerId(this.getPayer().getId());
        }
        data.setDate(LocalDate.from(this.getCreatedOn().atZone(ZoneId.systemDefault())));
        data.setComments(this.getComments());
        return data;
    }
}
