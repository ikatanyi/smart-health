package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Kelsas
 */
public interface StockMovement {
   public Long getId();
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    public LocalDate getTransDate();

    public Long getItemId();

    public String getItemCode();

    public String getItemName();

    public Long getStoreId();

    public String getStoreName();

    public String getDescription();

    public Double getReceived();

    public Double getIssued();

    public Double getBalance();

    public Double getPrice();

    public Double getTotal();

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    public LocalDateTime getCreateDate();

    public String getCreateBy();

}
