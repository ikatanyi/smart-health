package io.smarthealth.accounting.pricebook.domain;

import java.io.Serializable;
import javax.persistence.EmbeddedId;

/**
 *
 * @author Kelsas
 */
public class PriceItem implements Serializable {

    @EmbeddedId
    private PriceItemId id;

    private Double amount;

    public PriceItem() {
    }

    public PriceItem(PriceItemId id, Double amount) {
        this.id = id;
        this.amount = amount;
    }
}
