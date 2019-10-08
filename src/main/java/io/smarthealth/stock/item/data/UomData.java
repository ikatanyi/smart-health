package io.smarthealth.stock.item.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class UomData {

    private String categoryName;

    private String name;
    private double rounding;
    private String symbol;
    private double rate;
    private double factor;
    private boolean active;
}
