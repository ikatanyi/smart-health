package io.smarthealth.stock.purchase.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import io.smarthealth.stock.purchase.domain.enumeration.PurchaseInvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PurchaseInvoiceData {

    private Long supplierId;
    private String supplier;
    private String purchaseOrderNumber;
    private PurchaseInvoice.Type type;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private String transactionId;
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

}
