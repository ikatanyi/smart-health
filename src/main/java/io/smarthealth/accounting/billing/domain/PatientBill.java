/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.billing.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
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
@Table(name = "patient_bill")
public class PatientBill extends Auditable {

    @ManyToOne
    private Patient patient;
    @ManyToOne
    private Visit visit;
    private String paymentMode;
    private String billNumber; //can also be an invoice
    private String referenceNumber;
    private Double balance;
    private Double Amount;
    private LocalDate billingDate;

    @OneToMany(mappedBy = "patientBill")
    private List<PatientBillLine> billLines = new ArrayList<>();
    //

    public void addPatientBillLine(PatientBillLine billLine) {
        billLine.setPatientBill(this);
        billLines.add(billLine);
    }

    public void addPatientBillLine(List<PatientBillLine> billLine) {
        billLine.stream().map((bill) -> {
            bill.setPatientBill(this);
            return bill;
        }).forEachOrdered((bill) -> {
            billLines.add(bill);
        });
    }

}
