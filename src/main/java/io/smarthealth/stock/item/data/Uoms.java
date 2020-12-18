package io.smarthealth.stock.item.data;

import io.smarthealth.stock.item.domain.Uom;
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data
public class Uoms {

    private Long id;
    private String name;
    private String symbol;

    public static Uoms map(Uom uom) {
        Uoms um = new Uoms();
        um.setId(uom.getId());
        um.setName(uom.getName());
        um.setSymbol(uom.getSymbol());
        return um;
    }
}
