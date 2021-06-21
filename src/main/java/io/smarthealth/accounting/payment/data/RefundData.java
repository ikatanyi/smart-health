package io.smarthealth.accounting.payment.data;

import lombok.Getter;
import lombok.Setter;

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
    private String paymentMethod;
    private String referenceNumber;
    private String refundTo;
    private BigDecimal refundAmount;
    private LocalDate refundDate;
    private String comments;
    private List<ReceiptItemData> items = new ArrayList<>();
}
