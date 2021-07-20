package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class ItemValuation {
    private String code;
    private String description;
    private String category;
    private String active;
    private Double quantityOnHand;
    private BigDecimal cost;
    private BigDecimal value;

    public ItemValuation() {
    }

    public ItemValuation(String code, String description, ItemCategory category, boolean active, Double quantityOnHand, BigDecimal cost) {
        this.code = code;
        this.description = description;
        this.category = category!=null ? category.name() : "";
        this.active = active ? "Yes" : "No";
        this.quantityOnHand = quantityOnHand;
        this.cost = cost;

    }

    public BigDecimal getValue() {
        BigDecimal qty = BigDecimal.valueOf(quantityOnHand);
        value = qty.multiply(cost!=null ? cost : BigDecimal.ZERO);
        return value;
    }
}
