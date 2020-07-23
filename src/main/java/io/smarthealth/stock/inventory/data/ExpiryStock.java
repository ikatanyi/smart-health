package io.smarthealth.stock.inventory.data;

import java.time.LocalDate;

/**
 *
 * @author Kennedy.Ikatanyi
 */
public interface ExpiryStock {
    
    public Long getItemId();
    public String getItemCode();
    public String getItemName();

    public Long getStoreId();

    public String getStoreName();

    public Double getQuantity();

    public String getBatchNo();   
    
    public LocalDate getExpiryDate();
    
    public Integer getDays();
}
