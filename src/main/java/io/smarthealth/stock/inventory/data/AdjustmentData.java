package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Data
public class AdjustmentData {

    private Long id;
    @NotNull(message = "Store ID is Required")
    private Long storeId;
    private String storeName;
    
    private String adjustmentMode;
    
    private String referenceNo;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dateRecorded;
    
    private String stockAccountNumber;
    private String stockAccountName;
    
    private String reasons;
     
    private List<QuantityCountData> adjustments=new ArrayList<>();
    
}
