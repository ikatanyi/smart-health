package io.smarthealth.stock.purchase.data;

import io.smarthealth.stock.inventory.data.StockEntryData;
import io.smarthealth.stock.inventory.domain.StockEntry;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class SupplierInvoiceReport {
    private Long supplierId;
    private String supplier;
    private String taxNumber;
    private LocalDate date;
    private LocalDate dueDate;
    private String number;  //document number
    private String reference; //supplier reference
    private List<StockEntryData> invoiceItems =new ArrayList<>();

    public SupplierInvoiceReport() {
    }

    public SupplierInvoiceReport(PurchaseInvoice invoice) {
        if(invoice.getSupplier()!=null){
            this.supplierId = invoice.getSupplier().getId();
            this.supplier = invoice.getSupplier().getSupplierName();
            this.taxNumber = invoice.getSupplier().getTaxNumber();

        }
        this.date = invoice.getInvoiceDate();
        this.dueDate = invoice.getDueDate();
        this.number = invoice.getDocumentNumber();
        this.reference = invoice.getInvoiceNumber();
    }
    public SupplierInvoiceReport(PurchaseInvoice invoice, List<StockEntry> entries) {
        if(invoice.getSupplier()!=null){
            this.supplierId = invoice.getSupplier().getId();
            this.supplier = invoice.getSupplier().getSupplierName();
            this.taxNumber = invoice.getSupplier().getTaxNumber();

        }
        this.date = invoice.getInvoiceDate();
        this.dueDate = invoice.getDueDate();
        this.number = invoice.getDocumentNumber();
        this.reference = invoice.getInvoiceNumber();
        addInvoiceItem(entries);
    }

    public void addInvoiceItem(List<StockEntry> entries){
        invoiceItems = entries.stream()
                .map(StockEntry::toData)
                .collect(Collectors.toList());
    }
}
