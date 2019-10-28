package io.smarthealth.stock.item.data;

import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.enumeration.UomCategory;
import lombok.Data;
import org.modelmapper.ModelMapper;

/**
 *
 * @author Kelsas
 */
@Data
public class UomData {

    private Long id;
    private UomCategory category;
    private String name;
    private double rounding;
    private String symbol;
    private double rate;
    private double factor;
    private boolean active;

    public static Uom map(UomData data) {
        ModelMapper modelMapper = new ModelMapper();
        Uom uom = modelMapper.map(data, Uom.class);
        return uom;
    }

    public static UomData map(Uom uom) {
        ModelMapper modelMapper = new ModelMapper();
        UomData data = modelMapper.map(uom, UomData.class);
        return data;
    }
}
