package io.smarthealth.accounting.pricelist.domain;

import java.time.LocalDate;

/**
 *
 * @author Kelsas
 */
public interface PriceListDTO {

    public Long getId();

    public String getItemCode();

    public String getItemName();

    public String getItemType();

    public Double getDefaultRate();

    public Double getSpecialRate();

    public LocalDate getEffectiveDate();

    public Long getServicePointId();

    public String getServicePoint();

    public Long getPricebookId();

    public String getPricebook();
}
