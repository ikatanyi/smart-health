package io.smarthealth.stock.inventory.data;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Data
public class StockAdjustmentData {

    private Long id;

    private Long storeId;
    private String storeName;

    private Long itemId;
    private String item;
    private String itemCode;

    private LocalDateTime dateRecorded;

    private Double quantityBalance;
    private Double quantityCounted;
    private double quantityAdjusted;
    
    private String transactionId;
    private String reasons;

}
