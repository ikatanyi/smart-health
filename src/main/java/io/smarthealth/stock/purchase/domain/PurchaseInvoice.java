package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "purchase_invoice")
public class PurchaseInvoice extends Auditable {

    public enum Type {
        Stock_Delivery,
        Stock_Returns,
        Supplier_Bill
    }
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_invoice_supplier_id"))
    private Supplier supplier;

    private String purchaseOrderNumber;
    private Boolean paid;
    private Boolean isReturn; //debit note
    @Enumerated(EnumType.STRING)
    private Type type;
    private String invoiceNumber; //supplier invoice number
    private LocalDate invoiceDate; //supplier invoice date
    private LocalDate dueDate;
    private BigDecimal invoiceAmount;
    private BigDecimal tax;
    private BigDecimal discount; 
    private BigDecimal netAmount;
    private BigDecimal invoiceBalance;
    private LocalDate transactionDate;
    private String transactionNumber;
    private boolean approved= false;
    private String approvedBy;
    private LocalDate approvalDate;

    @Enumerated(EnumType.STRING)
    private PurchaseInvoiceStatus status;

    public PurchaseInvoiceData toData() {
        PurchaseInvoiceData data = new PurchaseInvoiceData();
        data.setId(this.getId());
        if (this.supplier != null) {
            data.setSupplierId(this.supplier.getId());
            data.setSupplier(this.supplier.getSupplierName());
        }
        data.setPurchaseOrderNumber(this.purchaseOrderNumber);
        data.setTransactionDate(this.transactionDate);
        data.setDueDate(this.dueDate);
        data.setPaid(this.paid);
        data.setIsReturn(this.isReturn);
        data.setInvoiceNo(this.invoiceNumber);
        data.setInvoiceAmount(this.invoiceAmount);
        data.setInvoiceBalance(this.invoiceBalance);
        data.setTax(this.tax);
        data.setDiscount(this.discount);
        data.setStatus(this.status);
        data.setType(this.type);
        data.setCreatedBy(this.getCreatedBy());
        data.setTransactionId(this.getTransactionNumber());
        data.setAge(ChronoUnit.DAYS.between(this.transactionDate, LocalDate.now()));
        data.setApproved(this.approved);
        data.setApprovedBy(this.approvedBy);
        
        return data;
    }

}
