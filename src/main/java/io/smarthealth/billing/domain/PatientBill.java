package io.smarthealth.billing.domain;

import io.smarthealth.billing.data.PatientBillData;
import io.smarthealth.billing.data.PatientBillItemData;
import io.smarthealth.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private String paymentMode;
    private String billNumber; //can also be an invoice
    private String referenceNumber;
    private Double balance;
    private Double Amount;
    private Double discount;
    private LocalDate billingDate;
    private String journalNumber;
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @OneToMany(mappedBy = "patientBill", cascade = CascadeType.ALL)
    private List<PatientBillItem> billLines = new ArrayList<>();
    //

    public void addBillItem(PatientBillItem billItem) {
        billItem.setPatientBill(this);
        billLines.add(billItem);
    }

    public void addBillItems(List<PatientBillItem> billItems) {
        this.billLines=billItems;
        this.billLines.forEach(x -> x.setPatientBill(this));
    }
     

    public PatientBillData toData() {
        PatientBillData data = new PatientBillData();
        data.setId(this.getId());
        data.setBillNumber(this.getBillNumber());
        data.setBillingDate(this.getBillingDate());
        data.setJournalNumber(this.getJournalNumber());
        data.setReferenceNumber(this.getReferenceNumber());
        data.setAmount(this.getAmount());
        data.setDiscount(this.getDiscount());
        data.setBalance(this.getBalance());
        data.setPaymentMode(this.getPaymentMode());
        data.setStatus(this.getStatus());

        if (this.getVisit() != null) {
            data.setVisitNumber(this.getVisit().getVisitNumber());
            data.setPatientNumber(this.getVisit().getPatient().getPatientNumber());
            data.setPatientName(this.getPatient().getFullName());
        }

        List<PatientBillItemData> billItems = this.getBillLines().stream()
                .map(b -> b.toData())
                .collect(Collectors.toList());

        data.setBillItems(billItems);

        return data;
    }

}
