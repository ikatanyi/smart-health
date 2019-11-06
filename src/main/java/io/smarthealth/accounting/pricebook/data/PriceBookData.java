/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pricebook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.stock.item.data.ItemSimpleData;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author Kelsas
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceBookData {

    private Long id;
    private String type;
    private String name;
    private String description;
    private Long currencyId;
    private String currency;
    private String priceBookType;
    private Double percentage;
    private Boolean isIncrease; //mark down or mark up
    private Double decimalPlace;

    private List<ItemSimpleData> pricebookItems;

    private String status;

    public static PriceBookData map(PriceBook pricebook) {
        PriceBookData data = new PriceBookData();
        data.setId(pricebook.getId());
        data.setType(pricebook.getType().name());
        data.setName(pricebook.getName());
        data.setDescription(pricebook.getDescription());
        data.setPercentage(pricebook.getPercentage());
        data.setIsIncrease(pricebook.getIncrease());
        data.setDecimalPlace(pricebook.getDecimalPlace());
        data.setStatus(pricebook.isActive() ? "active" : "inactive");
        
        if (pricebook.getCurrency() != null) {
            data.setCurrency(pricebook.getCurrency().getName());
            data.setCurrencyId(pricebook.getCurrency().getId());
        }

        if (pricebook.getPriceBookType() != null) {
            data.setPriceBookType(pricebook.getPriceBookType().name());
        }

        if (pricebook.getPriceBookItems() != null) {
            List<ItemSimpleData> list = pricebook.getPriceBookItems().stream().map(item -> ItemSimpleData.map(item)).collect(Collectors.toList());
            data.setPricebookItems(list);
        } 
        return data;
    }
}
