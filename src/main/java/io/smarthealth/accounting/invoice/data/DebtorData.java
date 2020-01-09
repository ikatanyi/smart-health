package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DebtorData {

    private Long paymentTermId;
    private String paymentTerms;
    
    private Long payerId;
    private Long schemeId;
    
    private Double amount;
    
    private String memberName;
    
    private String memberNo;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dueDate;
}
