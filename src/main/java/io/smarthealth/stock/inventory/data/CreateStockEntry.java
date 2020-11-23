package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.enumeration.MovementPurpose;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Stock Entry
 *
 * @author Kelsas
 */
@Data
public class CreateStockEntry {

    private Long id;

    private Long storeId;
    private String store; 
    private Long destinationStoreId;
    private String destinationStore;

    private String referenceNumber; //ref LPO,supplier, patient no
    private String deliveryNumber; //GRN| transaction reference
    private String transactionNumber; //auto generated ST-2019-00002 

    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate transactionDate;
    private MovementType movementType;
    private MovementPurpose movementPurpose;

    private List<StockItem> items=new ArrayList<>();
    

    /*
    Perpetual Inventory
        If perpetual inventory system is enabled, additional costs will be booked in "Expense Included In Valuation" account.
    Periodic
        Do not post stocks
     */
}
