package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.accounting.pricebook.data.PriceListData;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pricelist")
public class PriceList extends Auditable {
 
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pricelist_item_id"))
    private Item item;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_pricelist_service_point_id"))
    private ServicePoint servicePoint;
    
    private Boolean defaultPrice;
    private BigDecimal sellingRate;
    private LocalDate effectiveDate;
    private Boolean active;

    public PriceList(Item item, ServicePoint servicePoint, Double sellingRate) {
        this.item = item;
        this.servicePoint = servicePoint;
        this.defaultPrice = Boolean.TRUE;
        this.sellingRate = BigDecimal.valueOf(sellingRate);
        this.effectiveDate = LocalDate.now();
        this.active = Boolean.TRUE;
    }

    public PriceListData toData() {
        PriceListData data = new PriceListData();
        data.setId(this.getId());
        data.setActive(this.active);
        data.setDefaultPrice(this.defaultPrice);
        data.setSellingRate(this.sellingRate);
        if (this.item != null) {
            data.setItemId(this.item.getId());
            data.setItemCode(this.item.getItemCode());
            data.setItemName(this.item.getItemName());
            data.setItemCategory(this.item.getCategory());
            data.setItemType(this.item.getItemType());
            data.setCostRate(this.item.getCostRate());
            if (this.defaultPrice!=null && this.defaultPrice) {
                data.setSellingRate(this.item.getRate());
            }
        }
        if (this.servicePoint != null) {
            data.setServicePoint(this.servicePoint.getName());
            data.setServicePointId(this.item.getId());
        }
        return data;
    }
}
