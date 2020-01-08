package io.smarthealth.stock.inventory.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Data
public class AdjustmentData {

    private Long id;
    
    private Long storeId;
    private String storeName;
    
    private String adjustmentMode;
    
    private String referenceNo;
    
    private LocalDateTime dateRecorded;
    
    private String stockAccountNumber;
    private String stockAccountName;
    
    private String reasons;
     
    private List<QuantityCountData> adjustments=new ArrayList<>();
    
}
