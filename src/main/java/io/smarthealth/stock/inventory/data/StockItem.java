package io.smarthealth.stock.inventory.data;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StockItem implements Serializable {

    private Long itemId;
    private String itemCode;
    private String item;
    private Double quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private String unit;
}
