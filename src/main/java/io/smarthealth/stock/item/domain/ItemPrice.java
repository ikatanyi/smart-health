package io.smarthealth.stock.item.domain;

import io.smarthealth.accounting.payment.domain.PriceList;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.math.BigDecimal;
import javax.persistence.Entity;
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
public class ItemPrice extends Identifiable{
    @ManyToOne
    private Item item;
    
    private PriceList priceList;
    private BigDecimal rate;
    private Boolean enabled;
}
