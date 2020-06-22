package io.smarthealth.stock.inventory.data;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierStockItem extends StockItem {

    private Long purchaseOrderId;
    private Double qtyOrdered;
    private Double receivedQuantity;
    private String batchNumber;
    private LocalDate expiryDate;
}
