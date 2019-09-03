package io.smarthealth.financial.payment.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_payment_type")
public class PaymentType extends Identifiable {

    @Column(name = "value")
    private String name;
    private String description;
    private Boolean isCashPayment;
    @Column(name = "order_position")
    private Long position;
}
