package io.smarthealth.stock.item.data;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemPriceData {

    private Long itemId;
    private BigDecimal rate;
    private Boolean enabled;
}
