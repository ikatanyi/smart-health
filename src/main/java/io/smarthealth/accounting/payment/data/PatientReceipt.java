package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PatientReceipt {

    public  enum Type{
        Deposit,
        Payment
    }
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private BigDecimal amount;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod paymentMethod;
    private String reference;
    private String description;
    private String shiftNo;
    private Type receiptType;
    private String receivedFrom;
}
