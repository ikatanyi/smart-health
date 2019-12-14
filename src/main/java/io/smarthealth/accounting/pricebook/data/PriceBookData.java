package io.smarthealth.accounting.pricebook.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.accounting.pricebook.domain.PriceBook;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceCategory;
import io.smarthealth.accounting.pricebook.domain.enumeration.PriceType;
import io.smarthealth.stock.item.data.ItemSimpleData;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
//@Getter
//@Setter
//@Accessors(chain = true)
//@NoArgsConstructor
//@ToString
@Data
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceBookData {

    private Long id;
    private PriceCategory priceCategory;
    private String name;
    private String description;
    private Long currencyId;
    private String currency;
    private PriceType priceType;
    private Double percentage;
    private Boolean isIncrease; //mark down or mark up
    private Double decimalPlace;

    private List<ItemSimpleData> pricebookItems;

    private String status;

    public static PriceBookData map(PriceBook pricebook) {
        PriceBookData data = new PriceBookData();
        data.setId(pricebook.getId());
        data.setPriceCategory(pricebook.getPriceCategory());
        data.setPriceType(pricebook.getPriceType());
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

        if (pricebook.getPriceBookItems() != null && pricebook.getPriceBookItems().size() > 0) {
            List<ItemSimpleData> list = pricebook.getPriceBookItems().stream().map(item -> ItemSimpleData.map(item)).collect(Collectors.toList());
            data.setPricebookItems(list);
        }
        return data;
    }
}
