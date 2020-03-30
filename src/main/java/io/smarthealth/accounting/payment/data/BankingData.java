package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.BankingType;
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
public class BankingData {

    private Long id;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private String bankAccountNumber;
    private String bankAccountName;
    private String client;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal debit;
    private BigDecimal credit;
    private String paymentMode;
    private String referenceNumber; //voucher no,
     private BankingType transactionType;
    private String transactionNo;
    private String currency;
    private String createdBy;
}
