package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
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
public class SummaryBill {
    
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    public LocalDate date;
    public String visitNumber;
    public String patientNumber;
    public String patientName;
    public BigDecimal amount;
    public BigDecimal balance;
    public String paymentMethod;
    public Boolean isWalkin;

}
