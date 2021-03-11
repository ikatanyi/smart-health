package io.smarthealth.accounting.invoice.data.statement;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.billing.domain.enumeration.BillEntryType;
import io.smarthealth.accounting.invoice.domain.InvoiceItem;
import lombok.Data;

import java.math.BigDecimal;

public class InterimInvoiceItem {

    private Long itemId;
    private String itemCode;
    private String itemName;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal amount;
    private BigDecimal balance;
    private String transactionId;
    private String servicePoint;
    private Long servicePointId;
    private BillEntryType entryType;

    public static InterimInvoiceItem of(PatientBillItem billItem){
        InterimInvoiceItem items = new InterimInvoiceItem();
        items.setItemId(billItem.getItem().getId());
        items.setItemCode(billItem.getItem().getItemCode());
        items.setItemName(billItem.getItem().getItemName());
        items.setQuantity(billItem.getQuantity());
        items.setPrice(BigDecimal.valueOf(billItem.getPrice()));
        items.setAmount(BigDecimal.valueOf(billItem.getAmount()));
        items.setDiscount(BigDecimal.valueOf(billItem.getDiscount()));
        items.setEntryType(billItem.getEntryType());
        items.setServicePoint(billItem.getServicePoint());
        items.setServicePointId(billItem.getServicePointId());
        items.setTransactionId(billItem.getTransactionId());
        return items;
    }
    public static InterimInvoiceItem map(InvoiceItem invoiceItem){
        PatientBillItem billItem = invoiceItem.getBillItem();
        InterimInvoiceItem items = of(billItem);


        return items;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(String servicePoint) {
        this.servicePoint = servicePoint;
    }

    public Long getServicePointId() {
        return servicePointId;
    }

    public void setServicePointId(Long servicePointId) {
        this.servicePointId = servicePointId;
    }

    public BillEntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(BillEntryType entryType) {
        this.entryType = entryType;
    }
}
