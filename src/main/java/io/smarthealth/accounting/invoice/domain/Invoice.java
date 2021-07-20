package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.invoice.data.InvoiceData;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.accounting.invoice.data.InvoiceReceipt;
import io.smarthealth.accounting.payment.domain.Copayment;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.debtor.claim.creditNote.domain.CreditNote;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient_invoice")
//        , uniqueConstraints = {@UniqueConstraint(columnNames = {"number"}, name = "uk_invoice_number")}) //we add constraint later
public class Invoice extends Auditable {

    public enum TransactionType{
        Invoice,
        Payment,
        CreditNote
    }

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
    private String idNumber;

    @Column(name = "invoice_date")
    private LocalDate date;
    private LocalDate dueDate;
    private String number;  //invoice number
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal balance;
    private String transactionNo;
//    private LocalDateTime transactionDate;
//    @Enumerated(EnumType.STRING)
//    @Column(columnDefinition = "varchar(50) default 'Invoice'")
//    private TransactionType transactionType;
    private Boolean paid;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    private String notes;
    private Boolean awaitingSmart = Boolean.FALSE;
    @Column(name = "is_capitation_invoice")
    private Boolean capitation = Boolean.FALSE;
    @Column(name = "is_rebate")
    private Boolean rebate = Boolean.FALSE;
    private String preauthCode;

    @Transient
    private BigDecimal invoiceAmount; //temporarly holding for orginal invoice amount

    @Where(clause = "voided = false")
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice")
    private List<Copayment> copays = new ArrayList<>();

    @OneToMany(mappedBy = "invoice")
    private List<CreditNote> creditNotes = new ArrayList<>();

    public void addItem(InvoiceItem item) {
        item.setInvoice(this);
        item.setVoided(false);
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
            data.setAge(ChronoUnit.DAYS.between(this.date, LocalDate.now()));
            data.setVisitType(this.getVisit().getVisitType().name());
            if (this.visit.getPaymentDetails() != null) {
                data.setAuthorizationCode(this.visit.getPaymentDetails().getAuthorizationCode());
            } else {
                data.setAuthorizationCode("-");
            }
        }

        data.setIdNumber(this.idNumber);
        data.setMemberName(this.memberName);
        data.setMemberNumber(this.memberNumber);
        data.setInvoiceDate(this.date);
        data.setDueDate(this.dueDate);
        data.setNumber(this.number);
        data.setAmount(this.amount != null ? this.amount.setScale(0, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        data.setDiscount(this.discount);
        data.setTax(this.tax);
        data.setBalance(this.balance);
        data.setCreatedBy(this.getCreatedBy());
        data.setTransactionNo(this.transactionNo);
        data.setState(this.status != null ? this.status.name() : null);
        data.setAwaitingSmart(this.getAwaitingSmart());
        data.setInvoiceItems(
                this.items.stream()
//                        .filter(x -> x.getBillItem().getAmount() > 0)
                        .filter(x -> x.getBillItem().getEntryType() == BillEntryType.Debit)
                        .map(x -> x.toData())
                        .collect(Collectors.toList())
        );

        data.setInvoicePayments(
                this.items.stream()
//                        .filter(x -> x.getBillItem().getAmount() < 0)
                        .filter(x -> x.getBillItem().getEntryType() == BillEntryType.Credit || x.getBillItem().getAmount() < 0)
                        .map(x -> {
                            InvoiceReceipt.Type type = x.getBillItem().getItem().getCategory() == ItemCategory.CoPay ? InvoiceReceipt.Type.Copayment : InvoiceReceipt.Type.Receipt;
                            return new InvoiceReceipt(x.getId(), type, type.name(), x.getBillItem().getPaymentReference(), toBigDecimal(x.getBillItem().getAmount()), x.getBillItem().getBillingDate(), x.getBillItem().getItem().getCategory() != null ? x.getBillItem().getItem().getCategory().name() : "", x.getBillItem().getQuantity(), BigDecimal.valueOf(x.getBillItem().getPrice()));
                        })
                        .collect(Collectors.toList())
        );

        data.setCopay(
                this.items.stream()
                        .filter(x -> x.getBillItem().getItem().getCategory() == ItemCategory.CoPay)
                        .map(y -> BigDecimal.valueOf(y.getBillItem().getAmount()))
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );

        data.setPaid(
                this.items.stream()
                        .filter(x -> x.getBillItem().getItem().getCategory() != ItemCategory.CoPay && (x.getBillItem().getEntryType() == BillEntryType.Credit || x.getBillItem().getAmount() < 0))
                        .map(y -> BigDecimal.valueOf(y.getBillItem().getAmount()))
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );

        data.setTotalAmount(
                this.items.stream()
                        .filter(x -> x.getBillItem().getEntryType() == BillEntryType.Debit)
                        .map(y -> BigDecimal.valueOf(y.getBillItem().getAmount()))
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );

        data.setAmountDue(data.getTotalAmount().subtract((data.getPaid().add(data.getCopay()))));

        data.setCapitation(this.capitation);
        data.setRebate(this.rebate);
        data.setPreauthCode(this.preauthCode);

//        if(this.rebate){
//
//            InvoiceItemData inv = new InvoiceItemData();
//            inv.setInvoiceDate(this.date);
//            inv.setServicePoint("Rebate");
//            inv.setQuantity();
//        }
        if(this.visit!=null){
            data.setDischargeDate(this.visit.getStopDatetime() !=null  ? this.visit.getStopDatetime().toLocalDate() : this.visit.getStartDatetime().toLocalDate());
        }

        return data;
    }

    private BigDecimal toBigDecimal(Double val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(val);
    }

    public BigDecimal getLineTotals() {
        return java.math.BigDecimal.valueOf(this.items.stream()
                .map(x -> x.getBillItem().getAmount())
                .reduce(0D, (x, y) -> (x + y))
        );
    }
}
