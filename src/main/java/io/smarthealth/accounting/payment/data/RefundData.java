package io.smarthealth.accounting.payment.data;

import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RefundData {
    private String payer;
    private String shiftNo;
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;
    private String referenceNumber;
    private String refundTo;
    private BigDecimal refundAmount;
    private LocalDate refundDate;
    private String comments;
    private List<ReceiptItemData> items = new ArrayList<>();
}
