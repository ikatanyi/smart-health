package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseInvoiceData {

    private Long supplierId;
    private String supplier;
    private String purchaseOrderNumber;
    private String serialNumber; //ACC-PINV-2019-00001
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
    private Boolean paid;
    private Boolean isReturn; //debit note
    private String invoiceNo; //supplier invoice number
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate invoiceDate; //supplier invoice date
    private BigDecimal invoiceAmount;
    private BigDecimal invoiceBalance;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal netAmount;
    private PurchaseInvoiceStatus status;
    private String createdBy;

    public static PurchaseInvoiceData map(PurchaseInvoice invoice) {
        PurchaseInvoiceData data = new PurchaseInvoiceData();
        if (invoice.getSupplier() != null) {
            data.setSupplierId(invoice.getSupplier().getId());
            data.setSupplier(invoice.getSupplier().getSupplierName());
        }
        data.setPurchaseOrderNumber(invoice.getPurchaseOrderNumber());
        data.setSerialNumber(invoice.getSerialNumber());
        data.setTransactionDate(invoice.getTransactionDate());
        data.setDueDate(invoice.getDueDate());
        data.setPaid(invoice.getPaid());
        data.setIsReturn(invoice.getIsReturn());
        data.setInvoiceNo(invoice.getInvoiceNumber());
        data.setInvoiceAmount(invoice.getInvoiceAmount());
        data.setInvoiceBalance(invoice.getInvoiceBalance());
        data.setTax(invoice.getTax());
        data.setDiscount(invoice.getDiscount());
        data.setStatus(invoice.getStatus());
        data.setCreatedBy(invoice.getCreatedBy());

        return data;
    }
}
