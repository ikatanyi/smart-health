package io.smarthealth.financial.payment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_payment_details")
public class PaymentDetail extends Identifiable{
    @ManyToOne
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType; 
    private String accountNumber; 
    private String checkNumber; 
    private String routingCode; 
    private String receiptNumber; 
    private String bankNumber;
}
