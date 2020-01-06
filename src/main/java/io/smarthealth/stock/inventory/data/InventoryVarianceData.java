package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.*;
import io.smarthealth.stock.inventory.domain.enumeration.ModeofAdjustment;
import io.smarthealth.stock.item.data.ItemData;
import io.smarthealth.stock.stores.data.StoreData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *  Captures the Inventory Variance quantity and reasons
 *
 * @author Kelsas
 */
@Data
public class InventoryVarianceData {
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dateRecorded;
    private String accountNumber; 
    private String accountName; 
    private ModeofAdjustment adjustmentMode;
    private String reference;
    private String description;
    private List<VarItemData> itemData;
    private Long storeId;
    private String storeName;
    //use code - Stock Variance Reason
 
    public static InventoryVariance map(InventoryVarianceData data){
        InventoryVariance variance=new InventoryVariance();
        variance.setAdjustmentMode(data.getAdjustmentMode());
        variance.setDateRecorded(LocalDateTime.now());
        variance.setReference(data.getReference());
        return variance;
    }
}
