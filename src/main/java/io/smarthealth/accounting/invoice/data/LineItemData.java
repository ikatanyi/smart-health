package io.smarthealth.accounting.invoice.data;

import io.smarthealth.accounting.invoice.domain.LineItem; 
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data
public class LineItemData implements Serializable{

    private Long id;
    private Long itemId;
    private String itemCode;
    private String item;
    private String type; //indicate the service point -> la
    private String description;
    private Integer quantity;
    private Double unitCost;
    private Double amount;
    private Boolean discountable;
    private Double discount;
    private Boolean taxable;
    private Double tax;

    public static LineItemData map(LineItem lineItem) {
        LineItemData data = new LineItemData();
        data.setId(lineItem.getId());
        if (lineItem.getItem() != null) {
            data.setItemId(lineItem.getItem().getId());
            data.setItem(lineItem.getItem().getItemName());
            data.setItemCode(lineItem.getItem().getItemCode());
        }
        data.setType(lineItem.getType());
        data.setDescription(lineItem.getDescription());
        data.setQuantity(lineItem.getQuantity());
        data.setUnitCost(lineItem.getUnitCost());
        data.setAmount(lineItem.getAmount());
        return data;
    }
}
