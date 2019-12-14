package io.smarthealth.stock.item.domain;

import io.smarthealth.accounting.taxes.domain.Tax;
import io.smarthealth.stock.item.data.Uoms;
import io.smarthealth.stock.stores.data.StoreData;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ItemMetadata {

    private String code;
    private String message;
    private List<Uoms> uom;
    private List<Tax> taxes;
    private List<StoreData> stores; 
}
