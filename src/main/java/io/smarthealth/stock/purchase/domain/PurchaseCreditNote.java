package io.smarthealth.stock.purchase.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.purchase.data.PurchaseCreditNoteData;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.supplier.domain.Supplier;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier_credit_note")
public class PurchaseCreditNote extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_credit_note_supplier_id"))
    private Supplier supplier;
    private String number;
    private String invoiceNumber;
    private LocalDate creditDate;
    private BigDecimal amount;
    private String reason;
    private String TransactionId;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_credit_note_store_id"))
    private Store store;
    @OneToMany
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_purchase_credit_note_purchase_credit_note_item_id"))
    private List<PurchaseCreditNoteItem> items;
    
    public PurchaseCreditNoteData toData(){
        PurchaseCreditNoteData data = new PurchaseCreditNoteData();
        data.setAmount(this.getAmount());
        data.setCreditDate(this.getCreditDate());
        data.setCreditNoteNumber(this.getNumber());
        data.setInvoiceNumber(this.getInvoiceNumber());
        if(!this.items.isEmpty())
            data.setItems(
                    this.getItems()
                    .stream()
                    .map(item->(item.toData()))
                    .collect(Collectors.toList())
            );
        data.setReason(this.getReason());
        if(this.getSupplier()!=null){
           data.setSupplier(this.getSupplier().getSupplierName());
           data.setSupplierId(this.getSupplier().getId());
        }   
        if(this.getStore()!=null){
            data.setStore(this.getStore().getStoreName());
            data.setStoreId(this.getStore().getId());
        }
        data.setTransactionId(this.getTransactionId());
        return data;
    }
}
