package io.smarthealth.stock.inventory.data;

import lombok.Data;

import java.time.LocalDate;

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
