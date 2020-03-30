package io.smarthealth.accounting.payment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.purchase.domain.PurchaseInvoice;
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
@Table(name = "acc_supplier_payment")
public class SupplierPayment extends Identifiable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_inv_payment_pay_id"))
    private Payment payment;
    
    private LocalDate paymentDate;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_supplier_inv_payment_inv_id"))
    private PurchaseInvoice invoice;
    private BigDecimal amountPaid;
    private BigDecimal taxAmount;

    public SupplierPayment(Payment payment, PurchaseInvoice invoice, BigDecimal amountPaid, BigDecimal taxAmount) {
        this.payment = payment;
        this.invoice = invoice;
        this.amountPaid = amountPaid;
        this.taxAmount = taxAmount;
        this.paymentDate=payment.getPaymentDate();
    }

}
