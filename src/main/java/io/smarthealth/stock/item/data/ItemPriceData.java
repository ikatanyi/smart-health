package io.smarthealth.stock.item.data;

import java.math.BigDecimal;
import lombok.Data;

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
