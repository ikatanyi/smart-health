package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreAuthData {
    private String patientNumber;
    private String visitNumber;
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private String preauthCode;
//    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate preauthDate;
}
