package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_billing_item")
public class BillItem extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_bill_id"))
    private Bill patientBill; 
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_item_id"))
    private Item item;
    private LocalDate billingDate;
    private String transactionId;
    private Double quantity;
    private Double price;
    private Double discount;
    private Double taxes;
    private Double amount;
    private Double balance; 
    private String servicePoint;
    

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    public BillItemData toData() {
        BillItemData data = new BillItemData();
        data.setId(this.getId());
        if (this.getPatientBill() != null) {
            data.setBillNumber(this.getPatientBill().getBillNumber());
        }
        data.setBillingDate(this.getBillingDate());
        data.setPrice(this.getPrice());
        data.setQuantity(this.getQuantity());
        data.setAmount(this.getAmount());
        data.setTaxes(this.getTaxes());
        data.setBalance(this.getBalance());
        data.setDiscount(this.getDiscount());
        data.setTransactionId(this.getTransactionId());
        data.setStatus(this.getStatus());
        
        data.setCreatedBy(this.getCreatedBy());

        if (this.getItem() != null) {
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
            data.setItem(this.getItem().getItemName());
        }
        data.setServicePoint(this.getServicePoint()); 

        return data;
    }
}
