package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class CopaymentData {

    private Long id;
    private Long payerId;
    private String payerName;
    private Long schemeId;
    private String scheme;
    private String patientNumber;
    private String patientName;
    private String visitNumber;
    private String visitType;
    private String invoiceNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String receiptNumber;
    private BigDecimal amount;
    private Boolean paid;

}
