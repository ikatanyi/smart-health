package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.inventory.data.SupplierStockItem;
import io.smarthealth.stock.purchase.data.PurchaseInvoiceData;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import io.smarthealth.supplier.domain.Supplier;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_invoice_supplier_id"))
    private Supplier supplier;
    
    private String purchaseOrderNumber;
    private String serialNumber; //ACC-PINV-2019-00001
    private Boolean paid;
    private Boolean isReturn; //debit note
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
    
    @Enumerated(EnumType.STRING)
    private PurchaseInvoiceStatus status;
 
    public  PurchaseInvoiceData toData() {
        PurchaseInvoiceData data = new PurchaseInvoiceData();
        if (this.supplier != null) {
            data.setSupplierId(this.supplier.getId());
            data.setSupplier(this.supplier.getSupplierName());
        }
        data.setPurchaseOrderNumber(this.purchaseOrderNumber);
        data.setSerialNumber(this.serialNumber);
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
        data.setCreatedBy(this.getCreatedBy());

        return data;
    }
}
