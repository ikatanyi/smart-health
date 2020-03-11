package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
public class CreditorData {

    private Long id;
    private Long creditorId;
    private String creditorName;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String paymentMethod;
    private BigDecimal amount;
    private Long bankAccountId;
    private String bankAccountName;
    private String currency;
    private List<CreditorInvoiceData> invoices = new ArrayList<>();
}
