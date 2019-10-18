package io.smarthealth.stock.item.domain;

import io.smarthealth.accounting.payment.domain.PriceList;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "stock_item_price")
public class ItemPrice extends Identifiable {

    @ManyToOne
     @JoinColumn(foreignKey = @ForeignKey(name = "fk_price_item_id"))
    private Item item;

    private PriceList priceList;
    private BigDecimal rate;
    private Boolean enabled;
}
