package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.accounting.billing.data.BillItemData;
import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_billing")
public class PatientBill extends Auditable {
 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_patient_id"))
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_bill_visit_id"))
    private Visit visit;
    
    private LocalDate billingDate;    
    
    private String billNumber; //can also be an invoice
    
    private String paymentMode;
    private String transactionId;
    
    private Double discount;
    private Double taxes;
    private Double Amount;
    private Double balance;
    private String referenceNo;
     
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @OneToMany(mappedBy = "patientBill", cascade = CascadeType.ALL)
    private List<PatientBillItem> billItems = new ArrayList<>();
    //

    public void addBillItem(PatientBillItem billItem) {
        billItem.setPatientBill(this);
        billItems.add(billItem);
    }

    public void addBillItems(List<PatientBillItem> billItems) {
        this.billItems=billItems;
        this.billItems.forEach(x -> x.setPatientBill(this));
    }
     

    public BillData toData() {
        BillData data = new BillData();
        data.setId(this.getId());
        data.setBillNumber(this.getBillNumber());
        data.setBillingDate(this.getBillingDate());
        data.setReferenceNo(this.getReferenceNo());
        data.setTransactionId(this.getTransactionId());
        data.setAmount(this.getAmount());
        data.setDiscount(this.getDiscount());
        data.setDiscount(this.getDiscount());
        data.setBalance(this.getBalance());
        data.setPaymentMode(this.getPaymentMode());
        data.setStatus(this.getStatus());

        if (this.getVisit() != null) {
            data.setVisitNumber(this.getVisit().getVisitNumber());
            data.setPatientNumber(this.getVisit().getPatient().getPatientNumber());
            data.setPatientName(this.getPatient().getFullName());
        }

        List<BillItemData> list = this.getBillItems().stream()
                .map(b -> b.toData())
                .collect(Collectors.toList());

        data.setBillItems(list);

        return data;
    }

}
