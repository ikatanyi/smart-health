package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.enumeration.StatusType;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 * Balance Transaction Line of a given {@link Item } . It holds the current
 * balance information
 *
 * @author Kelsas
 */
@Data
public class InventoryBalanceData {

    private Long id;

    private Long storeId;
    private String storeName;

    private Long itemId;
    private String item;
    private String itemCode;

    private double availableStock;
}
