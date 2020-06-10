package io.smarthealth.accounting.doctors.domain;

import io.smarthealth.accounting.doctors.data.DoctorInvoiceData;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "acc_doctor_invoices",
        uniqueConstraints
        = @UniqueConstraint(columnNames = {"visit_id", "service_item_id"}, name = "UK_items_acc_doctor_invoices_visit_service")
) 
public class DoctorInvoice extends Auditable {

    public enum TransactionType {
        Debit,
        Credit
    }
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_invoice_doctor_id"))
    private Employee doctor;
    private LocalDate invoiceDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_invoice_patient_id"))
    private Patient patient;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_invoice_service_id"))
    private DoctorItem serviceItem;
    private String invoiceNumber;
    private Long billItemId;
    private Boolean paid;
    private BigDecimal amount;
    private BigDecimal balance;
    private String paymentMode;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String transactionId;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_invoice_visit_id"))
    private Visit visit;

    public DoctorInvoiceData toData() {
        DoctorInvoiceData data = new DoctorInvoiceData();
        data.setId(this.getId());
        data.setAmount(this.amount);
        data.setBalance(this.balance);
        if (this.doctor != null) {
            data.setDoctorId(this.doctor.getId());
            data.setDoctorName(this.doctor.getFullName());
            data.setStaffNumber(this.doctor.getStaffNumber());
        }
        data.setInvoiceDate(this.invoiceDate);
        data.setInvoiceNumber(this.invoiceNumber);
        data.setPaid(this.paid);
        if (this.patient != null) {
            data.setPatientName(this.patient.getFullName());
            data.setPatientNumber(this.patient.getPatientNumber());
        }
        data.setPaymentMode(this.paymentMode);
        if (this.serviceItem != null) {
            data.setServiceId(this.serviceItem.getId());
            data.setServiceName(this.serviceItem.getServiceType().getItemName());
        }
        data.setTransactionId(this.transactionId);
        data.setTransactionType(this.transactionType);
        return data;
    }
}
