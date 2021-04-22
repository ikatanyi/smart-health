package io.smarthealth.stock.inventory.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

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
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal amount;
    private String unit;
    private Long requistionId;
}
