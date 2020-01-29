package io.smarthealth.accounting.billing.domain;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PatientBillGroup {

    private String patientNumber;
    private String patientName;
    private LocalDate date;
    private String visitNumber;
    private Double amount;
    private Double balance;
    private String paymentMethod;

    public PatientBillGroup() {
    }

    public PatientBillGroup(String patientNumber, String patientName, LocalDate date, String visitNumber, Double amount, Double balance, String paymentMethod) {
        this.patientNumber = patientNumber;
        this.patientName = patientName;
        this.date = date;
        this.visitNumber = visitNumber;
        this.amount = amount;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
    }

     

}
