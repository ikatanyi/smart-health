package io.smarthealth.accounting.payment.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.payment.domain.enumeration.PayeeType;
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
public class MakePettyCashPayment {

    private String payee;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate date;
    private String description;
    private String referenceNumber;
    private BigDecimal approvedAmount;
    private List<PettyCashRequestItem> requests = new ArrayList<>();
}
