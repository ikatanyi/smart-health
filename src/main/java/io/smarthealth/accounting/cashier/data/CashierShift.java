package io.smarthealth.accounting.cashier.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.cashier.domain.ShiftStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Kelsas
 */
public interface CashierShift {

    String getCashPoint();

    public String getCashier();

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    public LocalDateTime getStartDate();

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    public LocalDateTime getEndDate();

    public String getShiftNo();

    public ShiftStatus getStatus();

    public BigDecimal getBalance();
   
    public Long getCashierId();
}
