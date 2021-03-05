package io.smarthealth.accounting.payment.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String paymentMethod;
    private String reference;
    private BigDecimal amount;
    private BigDecimal copayValue;
    private String copayType;
    private String shiftNo;
}
