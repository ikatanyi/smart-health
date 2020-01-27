package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.invoice.domain.InvoiceLineItem;
import io.smarthealth.infrastructure.lang.Constants;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceLineItemData implements Serializable {

    private Long id;
    private String invoiceNumber;
    private Long itemId;
    private String item;
    private String itemCode;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate billingDate;
    private String transactionId;

    private Double quantity;
    private Double price;

    private Double discount;
    private Double taxes;
    private Double amount;
    private Double balance;

    private String servicePoint;
    private String createdBy;

    public static InvoiceLineItemData map(InvoiceLineItem invoiceLineItem) {
        InvoiceLineItemData data = new InvoiceLineItemData();
       data.setInvoiceNumber(invoiceLineItem.getInvoice()!=null ? invoiceLineItem.getInvoice().getNumber() : "");
        data.setCreatedBy(invoiceLineItem.getCreatedBy());
        if (invoiceLineItem.getBillItem() != null) {
            PatientBillItem lineItem = invoiceLineItem.getBillItem();
            data.setId(lineItem.getId());
            if (lineItem.getItem() != null) {
                data.setItemId(lineItem.getItem().getId());
                data.setItem(lineItem.getItem().getItemName());
                data.setItemCode(lineItem.getItem().getItemCode());
            }

            data.setId(lineItem.getId());
            data.setTransactionId(lineItem.getTransactionId());
            data.setQuantity(lineItem.getQuantity());
            data.setPrice(lineItem.getPrice());
            data.setDiscount(lineItem.getDiscount());
            data.setTaxes(lineItem.getTaxes());
            data.setAmount(lineItem.getAmount());
            data.setBalance(lineItem.getBalance());
            data.setServicePoint(lineItem.getServicePoint());
        }
        return data;
    }
}
