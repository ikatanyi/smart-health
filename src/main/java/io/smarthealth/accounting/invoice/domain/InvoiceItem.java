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
    private String remarks;
    private LocalDateTime voidedDatetime;

    public InvoiceItem of(PatientBillItem item){
        InvoiceItem invoiceItem =new InvoiceItem();
        invoiceItem.setBillItem(item);
       return invoiceItem;
    }
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
        data.setMemberName(this.invoice.getMemberName());
        data.setMemberNo(this.invoice.getMemberNumber());
        data.setRemarks(this.getRemarks());
        data.setPayer(this.invoice.getPayer().getPayerName());
        data.setScheme(this.invoice.getScheme().getSchemeName());
        return data;
    }

    public static InvoiceItemData toData(PatientBillItem item) {
        InvoiceItemData data = new InvoiceItemData();
        data.setId(item.getId());
        data.setDate(item.getBillingDate());
        data.setInvoiceNo(item.getPaymentReference());
        data.setInvoiceDate(item.getBillingDate());
        data.setItemId(item.getItem().getId());
        data.setItemCode(item.getItem().getItemCode());
        data.setItem(item.getItem().getItemName());
        data.setQuantity(item.getQuantity());
        data.setPrice(toBigDecimal(item.getPrice()));
        data.setDiscount(toBigDecimal(item.getDiscount()));
        data.setTax(toBigDecimal(item.getTaxes()));
        data.setAmount(toBigDecimal(item.getAmount()));
        data.setTransactionId(item.getTransactionId());
        data.setServicePointId(item.getServicePointId());
        data.setServicePoint(item.getServicePoint());

        return data;
    }

    private static BigDecimal toBigDecimal(Double val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(val);
    }
}
