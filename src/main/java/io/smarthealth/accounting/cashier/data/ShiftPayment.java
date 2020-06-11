package io.smarthealth.accounting.cashier.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
public interface ShiftPayment {

    public Long getShiftId();

    public String getShiftNo();

    public Long getCashierId();

    public String getCashier();

    public String getCashierName();

    public String getMethod();

    public BigDecimal getTotal();

}
