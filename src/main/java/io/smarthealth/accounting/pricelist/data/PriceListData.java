package io.smarthealth.accounting.pricelist.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import io.smarthealth.stock.item.domain.enumeration.ItemType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class PriceListData {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String itemName;
    private ItemCategory itemCategory;
    private ItemType itemType;
    private BigDecimal sellingRate;
    private BigDecimal costRate;

    private Long servicePointId;
    private String servicePoint;

    private Boolean defaultPrice;
    private boolean excluded;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate effectiveDate;

    private Boolean active;

}
