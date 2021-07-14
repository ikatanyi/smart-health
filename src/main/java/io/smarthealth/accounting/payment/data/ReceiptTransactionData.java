package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptAndPaymentMethod;
import io.smarthealth.accounting.payment.domain.enumeration.TrnxType;
import io.smarthealth.infrastructure.lang.Constants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Kelsas
 */
@Data
public class ReceiptTransactionData {

    private Long id;

    private String payer;
    private String description;
    private String receiptNo;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime datetime;

    @Enumerated(EnumType.STRING)
    private ReceiptAndPaymentMethod method;
    private BigDecimal amount = BigDecimal.ZERO;
    private String reference;
    private TrnxType type;
    private String currency;

}
