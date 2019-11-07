package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.auth.domain.User;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Department;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
}
