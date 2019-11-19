package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.account.domain.enumeration.TransactionType;
import io.smarthealth.accounting.billing.data.PatientBillItemData;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "patient_billing_item")
public class PatientBillItem extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_bill_id"))
    private PatientBill patientBill;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_item_item_id"))
    private Item item;
    private LocalDate billingDate;
    private Double quantity;
    private Double price;
    private Double amount;
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
