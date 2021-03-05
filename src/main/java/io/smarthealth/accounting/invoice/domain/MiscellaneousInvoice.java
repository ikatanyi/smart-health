package io.smarthealth.accounting.invoice.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.smarthealth.debtor.payer.domain.Payer;
import io.smarthealth.debtor.payer.domain.Scheme;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import io.smarthealth.accounting.invoice.domain.MiscellaneousInvoiceItem;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_misc_invoices")
public class MiscellaneousInvoice extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_misc_invoices_payer_id"))
    private Payer payer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_misc_invoices_scheme_id"))
    private Scheme scheme;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String reference;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<MiscellaneousInvoiceItem> lineItems = new ArrayList<>();

    public void addItem(MiscellaneousInvoiceItem item) {
        item.setInvoice(this);
        lineItems.add(item);
    }

    public void addItems(List<MiscellaneousInvoiceItem> items) {
        this.lineItems = items;
        this.lineItems.forEach(x -> x.setInvoice(this));
    }
}
