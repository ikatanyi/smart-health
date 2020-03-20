package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
public class CashBookData {

    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private String payee;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal debit;
    private BigDecimal credit;
    private String paymentMode;
    private String referenceNumber; //voucher no,
    private String transactionType;
    private String transactionNo;
    private String currency;
    private String createdBy;
}
