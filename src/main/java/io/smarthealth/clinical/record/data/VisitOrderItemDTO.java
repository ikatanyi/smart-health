package io.smarthealth.clinical.record.data;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VisitOrderItemDTO {

    private Long requestId;
    private Long itemId;
    private String itemName;
    private String itemCode;
    private ItemCategory itemCategory;
    private Double quantity = 1.0;
    private BigDecimal price;
    private BigDecimal amount;
    private String requestType;

    public VisitOrderItemDTO() {
    }

    public VisitOrderItemDTO(Long requestId, Long itemId, String itemName, String itemCode, ItemCategory itemCategory, Double quantity, BigDecimal price, DoctorRequestData.RequestType requestType) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.itemCategory = itemCategory;
        this.quantity = quantity;
        this.price = price;
        this.amount = (price!=null ? price.multiply(BigDecimal.valueOf(quantity)): BigDecimal.ZERO);
        this.requestType = requestType!=null ? requestType.name() : "";
    }
}
