package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
public interface BillSummary {

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    public LocalDate getDate();

    public String getVisitNumber();
    
//    @Value("#{@billingUtil.managePatientNumber(target)}")
    public String getPatientNumber();

    public String getPatientName();

    public BigDecimal getAmount();

    public BigDecimal getBalance();

    public String getPaymentMethod();

    public Boolean getWalkin();
   
}
