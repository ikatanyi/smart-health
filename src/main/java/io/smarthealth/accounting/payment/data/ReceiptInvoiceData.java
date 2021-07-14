package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptInvoiceData {

    private String description;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String schemeName;
    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;
    private String reference;
    private BigDecimal amount;
    private BigDecimal copayValue;
    private String copayType;
    private String shiftNo;
}
