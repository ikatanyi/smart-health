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
public class CreditorInvoiceData {
    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String invoiceNo;
    private BigDecimal amount;
    private BigDecimal balance;
    
}
