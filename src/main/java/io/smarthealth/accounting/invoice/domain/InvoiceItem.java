package io.smarthealth.accounting.invoice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.invoice.data.InvoiceItemData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "patient_invoice_items")
public class InvoiceItem extends Auditable {

    @ManyToOne
    @JoinColumn(name = "invoice_id", foreignKey = @ForeignKey(name = "fk_invoiceitem_invoice_id"))
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "item_id", foreignKey = @ForeignKey(name = "fk_invoiceitem_bill_item_id"))
    private PatientBillItem billItem;

    private BigDecimal balance;
    
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidedDatetime;

    public InvoiceItemData toData() {
        InvoiceItemData data = new InvoiceItemData();
        data.setId(this.getId());
        data.setDate(this.billItem.getBillingDate());
        data.setInvoiceNo(this.invoice.getNumber());
        data.setInvoiceDate(this.invoice.getDate());
        data.setItemId(this.billItem.getItem().getId());
        data.setItemCode(this.billItem.getItem().getItemCode());
        data.setItem(this.billItem.getItem().getItemName());
        data.setQuantity(this.billItem.getQuantity());
        data.setPrice(toBigDecimal(this.billItem.getPrice()));
        data.setDiscount(toBigDecimal(this.billItem.getDiscount()));
        data.setTax(toBigDecimal(this.billItem.getTaxes()));
        data.setAmount(toBigDecimal(this.billItem.getAmount()));
        data.setTransactionId(this.billItem.getTransactionId());
        data.setServicePointId(this.billItem.getServicePointId());
        data.setServicePoint(this.billItem.getServicePoint());
        return data;
    }

    private BigDecimal toBigDecimal(Double val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(val);
    }
}
