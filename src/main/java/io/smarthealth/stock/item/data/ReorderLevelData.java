package io.smarthealth.stock.item.data;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ReorderLevelData {

    private Long id;
    private Long storeId;
    private String store;
    private Long itemId;
    private String itemCode;
    private String item;
    private double reorderLevel;
    private double orderQuantity;
    private boolean active;

}
