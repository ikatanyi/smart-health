package io.smarthealth.common.domain;

import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
public interface BillableItem {

    public String getCode();

    public BigDecimal getUnitPrice();
}
