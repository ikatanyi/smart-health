package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient_invoice")
//        , uniqueConstraints = {@UniqueConstraint(columnNames = {"number"}, name = "uk_invoice_number")}) //we add constraint later
public class Invoice extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_payer_id"))
    private Payer payer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_scheme_id"))
    private Scheme scheme;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_patient_id"))
    private Patient patient;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_invoices_visit_id"))
    private Visit visit;

    private String memberNumber;
    private String memberName;
    private String terms;

    @Column(name = "invoice_date")
    private LocalDate date;
    private LocalDate dueDate;
    private String number;  //invoice number
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal balance;
    private String transactionNo;
    private Boolean paid;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    private String notes;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice")
    private List<Copayment> copays = new ArrayList<>();
    
     @OneToMany(mappedBy = "invoice")
    private List<CreditNote> creditNotes = new ArrayList<>();

    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        items.add(item);
    }

    public void addItems(List<InvoiceItem> items) {
        this.items = items;
        this.items.forEach(x -> x.setInvoice(this));
    }

    public InvoiceData toData() {
        InvoiceData data = new InvoiceData();
        data.setId(this.getId());
        if (this.payer != null) {
            data.setPayerId(this.payer.getId());
            data.setPayer(this.payer.getPayerName());
            data.setTerms(this.payer.getPaymentTerms() != null ? this.payer.getPaymentTerms().getTermsName() : "");
        }

        if (this.scheme != null) {
            data.setScheme(this.scheme.getSchemeName());
            data.setSchemeId(this.scheme.getId());
        }
        if (this.patient != null) {
            data.setPatientNumber(this.patient.getPatientNumber());
            data.setPatientName(this.patient.getFullName());
        }
        if (this.visit != null) {
            data.setVisitNumber(this.visit.getVisitNumber());
            data.setVisitDate(this.visit.getStartDatetime().toLocalDate());
        }
        data.setMemberName(this.memberName);
        data.setMemberNumber(this.memberNumber);
        data.setInvoiceDate(this.date);
        data.setDueDate(this.dueDate);
        data.setNumber(this.number);
        data.setAmount(this.amount);
        data.setDiscount(this.discount);
        data.setTax(this.tax);
        data.setBalance(this.balance);
        data.setTransactionNo(this.transactionNo);
        data.setInvoiceItems(this.items.stream()
                .map(x -> x.toData())
                .collect(Collectors.toList())
        );
        return data;
    }
}
