package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;  
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity 
@Table(name = "invoices")
public class Invoice extends Auditable {
    
    private String payer;
    private String number;
    private String name = "Invoice"; //for internam use
    private String currency;
    private Boolean draft; // Outstanding true or false 
    private Boolean closed; // bad debt or not
    private Boolean paid; // fully paid or not
    
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status; //tracking status for the invoice

    @Column(name = "invoice_date")
    private LocalDate date;
    private LocalDate dueDate;
    private String paymentTerms; // 'Net 30'
    
    @OneToMany(mappedBy = "invoice",cascade = CascadeType.ALL)
    private List<LineItem> items=new ArrayList<>();
    private String notes; // additional notes displayed on invoice
    private Double subtotal;
//    private List<Discount> disounts;
//    private List<Tax> taxes;
    private Double total;
    private Double balance;

//    @ManyToOne
//    private Address shipTo; // include the supplier address here
    // Inoivce
    
      public void addItem(LineItem item) {
        item.setInvoice(this);
        items.add(item);
    }

    public void addItems(List<LineItem> items) {
        this.items=items;
        this.items.forEach(x -> x.setInvoice(this));
    }
}
