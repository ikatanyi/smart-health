/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.billing.domain.PatientBillItem;
import io.smarthealth.accounting.payment.data.ReceiptItemData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
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
@Table(name = "acc_receipt_items")
public class ReceiptItem extends Identifiable {

    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipts_billed_receipt_id"))
    private Receipt receipt;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipts_billed_items_id"))
    private PatientBillItem item;
    
    private Double quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal taxes;
    private BigDecimal amountPaid;

    public ReceiptItem(PatientBillItem item, BigDecimal amountPaid) {
        this.item = item;
        this.amountPaid = amountPaid;
    }

    public ReceiptItem(PatientBillItem item, Double quantity, BigDecimal price, BigDecimal discount, BigDecimal taxes, BigDecimal amountPaid) {
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.taxes = taxes;
        this.amountPaid = amountPaid;
    }
    
    public ReceiptItemData toData(){
        ReceiptItemData data = new ReceiptItemData();
        data.setReceiptNumber(this.getReceipt().getReferenceNumber());
        if(item!=null){
            data.setItemName(this.getItem().getItem().getItemName());
            data.setItemCode(this.getItem().getItem().getItemCode());
            data.setAmountPaid(this.getAmountPaid());
            data.setPrice(BigDecimal.valueOf(this.getItem().getPrice()));
            data.setQuantity(this.getItem().getQuantity());
            data.setTaxes(BigDecimal.valueOf(this.getItem().getTaxes()));
        }
        return data;
    }
}
