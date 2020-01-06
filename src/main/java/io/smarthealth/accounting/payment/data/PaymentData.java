package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentData {

    private Long id;

    private String method;

    private Double amount;

    private String referenceCode;

    private String type;

    private String currency;

    public static PaymentData map(Payment payment) {
        return new PaymentData(
                payment.getId(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getReferenceCode(),
                payment.getType(),
                payment.getCurrency()
        );
    }
}
