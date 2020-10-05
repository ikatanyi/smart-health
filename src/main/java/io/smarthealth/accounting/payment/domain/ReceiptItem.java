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
    private Boolean voided=Boolean.FALSE;
    private String voidedBy;
    private LocalDateTime voidedDate;

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
    public static ReceiptItem createReceipt(PatientBillItem bill){
//        System.err.println("creating a receipti details "+ bill.getId()+" namr "+bill.getPatientBill().getPatient().getFullName());
//        System.err.println(bill.toData());
        return new ReceiptItem(bill, bill.getQuantity(), BigDecimal.valueOf(bill.getPrice()), BigDecimal.valueOf(bill.getDiscount()), BigDecimal.valueOf(bill.getTaxes()), BigDecimal.valueOf(bill.getAmount()));
    }
    
    public ReceiptItemData toData(){
        ReceiptItemData data = new ReceiptItemData();
        data.setId(this.getId());
        data.setReceiptNumber(this.getReceipt().getReceiptNo());
        data.setPaymentMode(this.getReceipt().getPaymentMethod());
        if(this.getReceipt()!=null){
            data.setPatientName(this.getReceipt().getPayer());
            data.setReferenceNumber(this.getReceipt().getReceiptNo());
            data.setReceiptNumber(this.getReceipt().getReceiptNo());
        }
        if(item!=null){
            data.setItemName(this.getItem().getItem().getItemName());
            data.setItemCode(this.getItem().getItem().getItemCode());
            data.setAmountPaid(this.getAmountPaid());
            data.setPrice(toBigDecimal(this.getItem().getPrice()));
            data.setQuantity(this.getItem().getQuantity());
            data.setTaxes(toBigDecimal(this.getItem().getTaxes()));
            data.setTransactionDate(this.getItem().getBillingDate());
            if(this.getItem().getServicePoint()!=null)
                data.setServicePoint(this.getItem().getServicePoint());
            else
                data.setServicePoint("other");
        }
        return data;
    }
       private BigDecimal toBigDecimal(Double val){
        if(val==null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(val);
    }
}
