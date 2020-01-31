package io.smarthealth.stock.inventory.data;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SupplierStockItem extends StockItem {

    private Double qtyOrdered;
    private String batchNumber;
    private LocalDate expiryDate;
}
