package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.doctors.domain.DoctorInvoice;
import io.smarthealth.accounting.payment.data.SupplierPaymentData;
import io.smarthealth.infrastructure.domain.Identifiable;
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
@Table(name = "acc_doctors_payment")
public class DoctorsPayment extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_inv_payments_id"))
    private Payment payment;

    private LocalDate paymentDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_inv_invoice_id"))
    private DoctorInvoice invoice;
    private BigDecimal amountPaid;
    private BigDecimal taxAmount;

    public DoctorsPayment(Payment payment, DoctorInvoice invoice, BigDecimal amountPaid, BigDecimal taxAmount) {
        this.payment = payment;
        this.invoice = invoice;
        this.amountPaid = amountPaid;
        this.taxAmount = taxAmount;
    }

    public SupplierPaymentData toData(){
        SupplierPaymentData data = new SupplierPaymentData();
        data.setAmountPaid(this.getAmountPaid());
        if(this.getInvoice()!=null){
            data.setInvoiceAmount(this.getInvoice().getAmount());
            data.setInvoiceNumber(this.getInvoice().getInvoiceNumber());
            data.setInvoiceDate(this.getInvoice().getInvoiceDate());
            data.setDescription(this.getInvoice().getInvoiceNumber()+" - Consultancy");
        }
        data.setTaxAmount(this.getTaxAmount());
        return data;
    }
}
