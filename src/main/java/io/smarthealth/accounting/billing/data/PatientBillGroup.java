package io.smarthealth.accounting.billing.data;

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
    private String billNumber;
    private String transactionNo;

    public PatientBillGroup() {
    }

    public PatientBillGroup(String patientNumber, String patientName, LocalDate date, String visitNumber, Double amount, Double balance, String paymentMethod,String billNumber,String transactionNo) {
        this.patientNumber = patientNumber;
        this.patientName = patientName;
        this.date = date;
        this.visitNumber = visitNumber;
        this.amount = amount;
        this.balance = balance;
        this.paymentMethod = paymentMethod;
        this.billNumber=billNumber;
        this.transactionNo=transactionNo;
    }
}
