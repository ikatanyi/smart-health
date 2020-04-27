package io.smarthealth.accounting.billing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_billing_item")
//@NamedQuery(name = "t", query = "SELECT b.patientBill.billingDate as date, b.patientBill.reference as visitNumber, b.patientBill.reference as patientNumber, b.patientBill.otherDetails as patientName, SUM(b.amount) as amount, SUM(b.balance) as balance, b.patientBill.visit.paymentMethod as paymentMethod, 'True' as walkin FROM PatientBillItem b WHERE b.patientBill.reference =:visit GROUP BY 2,3")
public class PatientBillItem extends Auditable {

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_bill_id"))
    private PatientBill patientBill;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_item_id"))
    private Item item;

    private LocalDate billingDate;
    private String transactionId;
    private Double quantity;
    private Double price; 
    @Column(columnDefinition = "default 0")
    private Double discount =0.0;
    @Column(columnDefinition = "default 0")
    private Double taxes =0.0;
    
    private Double amount=0.0;
    private Double balance=0.0;
    private String servicePoint;
    private Long servicePointId;
    private Boolean paid;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Transient
    private Long medicId;
    private Long requestReference;
    /**
     * Reference payment details i.e. Receipt or an Invoice used to settle this
     * bill
     */
    private String paymentReference;

    public BillItemData toData() {
        BillItemData data = new BillItemData();
        data.setId(this.getId());
        if (this.getPatientBill() != null) {
            data.setBillNumber(this.patientBill.getBillNumber());
            data.setVisitNumber(this.patientBill.getVisit() != null ? this.patientBill.getVisit().getVisitNumber() : null);
            if (this.patientBill.getPatient() != null) {
                data.setPatientName(this.patientBill.getPatient().getFullName());
                data.setPatientNumber(this.patientBill.getPatient().getPatientNumber());

            }
        }
        data.setBillingDate(this.billingDate);
        data.setPrice(this.price);
        data.setQuantity(this.quantity);
        data.setAmount(this.amount);
        data.setTaxes(this.taxes);
        data.setBalance(this.balance);
        data.setDiscount(this.discount);
        data.setTransactionId(this.transactionId);
        data.setStatus(this.status);

        data.setCreatedBy(this.getCreatedBy());

        if (this.item != null) {
            data.setItemId(this.item.getId());
            data.setItemCode(this.item.getItemCode());
            data.setItem(this.item.getItemName());
        }
        data.setServicePoint(this.servicePoint);
        data.setServicePointId(this.servicePointId);
        data.setPaid(this.paid);
        data.setRequestReference(this.requestReference);
        data.setPaymentReference(this.paymentReference);

        if(this.getPatientBill().getWalkinFlag()!=null && this.getPatientBill().getWalkinFlag() ){
            data.setPatientName(this.getPatientBill().getOtherDetails());
            data.setPatientNumber(this.getPatientBill().getReference());           
        }
        data.setWalkinFlag(this.getPatientBill().getWalkinFlag());
        return data;
    }

}
