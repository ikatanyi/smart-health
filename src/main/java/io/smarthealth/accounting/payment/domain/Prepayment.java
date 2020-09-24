package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.payment.data.PrepaymentData;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_prepayments")
public class Prepayment extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_prepayment_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_prepayment_receipt_id"))
    private Receipt receipt;
    private LocalDate paymentDate;
    private String memo;
    private String paymentMethod;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;

    public PrepaymentData toData() {
        PrepaymentData data = new PrepaymentData();
        data.setAmount(this.amount);
        data.setBalance(this.balance);
        data.setCurrency(this.currency);
        data.setMemo(this.memo);
        data.setPatientName(this.patient.getFullName());
        data.setPatientNumber(this.patient.getPatientNumber());
        data.setPaymentDate(this.paymentDate);
        data.setPaymentMethod(this.paymentMethod);
        data.setReceiptNo(this.receipt.getReceiptNo());
        data.setReferenceNo(this.receipt.getReferenceNumber());
        data.setShiftNo(this.receipt.getShift().getShiftNo());
        data.setTransactionNo(this.receipt.getTransactionNo());

        return data;
    }
}
