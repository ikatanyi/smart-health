package io.smarthealth.stock.inventory.domain;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.stock.item.data.ItemData;
import java.util.List;

/**
 *
 * @author Kelsas
 */
public class VarianceMetadata {

    private String code;
    private String message;
    private List<CodeValueData> reasons;
    private List<ItemData> items;
}
