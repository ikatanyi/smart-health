package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString; 

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices", uniqueConstraints = { @UniqueConstraint( columnNames = { "number" }, name = "uk_invoice_number")})
public class Invoice extends Auditable {

    @OneToMany(mappedBy = "invoice")
    private List<CreditNote> creditNotes = new ArrayList<>();;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_payer_id"))
    private Payer payer;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_scheme_id"))
    private Scheme payee;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_bill_id"))
    private PatientBill bill;

    @Column(name = "invoice_date")
    private LocalDate date;

    private LocalDate dueDate;

    private String terms; // 'Net 30'

    private String transactionNo;

    private String reference;
//    @NaturalId
    private String number;  //invoice number
    private String currency;
    private Boolean draft; // Outstanding true or false 
    private Boolean closed; // bad debt or not
    private Boolean paid; // fully paid or not
    private Boolean isVerified; // fully paid or not
    private Double subtotal;
    private Double disounts;
    private Double taxes;
    private Double total;
    private Double balance;
    private String notes; // additional notes displayed on invoice
    

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status; //tracking status for the invoice

    @ToString.Exclude
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceLineItem> items = new ArrayList<>();

     @Transient
    protected boolean invoiceNumberRequiresAutoGeneration = false;
     
//    @ManyToOne
//    private Address shipTo; // include the supplier address here
    // Inoivce
    public void addItem(InvoiceLineItem item) {
        item.setInvoice(this);
        items.add(item);
    }

    public void addItems(List<InvoiceLineItem> items) {
        this.items = items;
        this.items.forEach(x -> x.setInvoice(this));
    }
    
    public void addCreditNoteItem(CreditNote item) {
        item.setInvoice(this);
        creditNotes.add(item);
    }

    public void addCreditNoteItems(List<CreditNote> items) {
        this.creditNotes = items;
        this.creditNotes.forEach(x -> x.setInvoice(this));
    }
    
}
