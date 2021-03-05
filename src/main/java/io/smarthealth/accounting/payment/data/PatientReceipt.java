package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.Builder;
import lombok.Data;

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
    private String paymentMethod;
    private String reference;
    private String description;
    private String shiftNo;
    private Type receiptType;
    private String receivedFrom;
}
