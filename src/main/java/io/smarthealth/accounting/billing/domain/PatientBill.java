package io.smarthealth.accounting.billing.domain;

import io.smarthealth.accounting.billing.domain.enumeration.BillStatus;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
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
    private LocalDate billingDate;
    private String journalNumber;
     @Enumerated(EnumType.STRING)
    private BillStatus status;

    @OneToMany(mappedBy = "patientBill")
    private List<PatientBillItem> billLines = new ArrayList<>();
    //

    public void addPatientBillLine(PatientBillItem billLine) {
        billLine.setPatientBill(this);
        billLines.add(billLine);
    }

    public void addPatientBillLine(List<PatientBillItem> billLine) {
        billLine.stream().map((bill) -> {
            bill.setPatientBill(this);
            return bill;
        }).forEachOrdered((bill) -> {
            billLines.add(bill);
        });
    }
    //

}
