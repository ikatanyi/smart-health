package io.smarthealth.billing.domain;

import io.smarthealth.billing.data.PatientBillItemData;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_billing_item")
public class PatientBillItem extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_bill_id"))
    private PatientBill patientBill; 
    private String transactionType;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_item_id"))
    private Item item;
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private Double amount;
    private Double discount;
    private String transactionNo;
    private Long servicePointId;
    private String servicePoint;
    private Double balance;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    public PatientBillItemData toData() {
        PatientBillItemData data = new PatientBillItemData();
        if (this.getPatientBill() != null) {
            data.setBillNumber(this.getPatientBill().getBillNumber());
        }
        data.setBillingDate(this.getBillingDate());
        data.setPrice(this.getPrice());
        data.setQuantity(this.getQuantity());
        data.setAmount(this.getAmount());
        data.setDiscount(this.getDiscount());
        data.setTransactionNo(this.getTransactionNo());
        
        data.setCreatedBy(this.getCreatedBy());

        if (this.getItem() != null) {
            data.setItemId(this.getItem().getId());
            data.setItemCode(this.getItem().getItemCode());
            data.setItem(this.getItem().getItemName());
        }
        data.setServicePoint(this.getServicePoint());
        data.setServicePointId(this.getServicePointId());

        return data;
    }
}
