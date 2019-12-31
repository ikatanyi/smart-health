package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.accounting.pricebook.data.ServiceItemData;
import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
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
@Table(name = "service_items")
public class ServiceItem extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_item_service_id"))
    private Item serviceType;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_service_item_point_id"))
    private ServicePoint servicePoint;

    private Double rate;

    private LocalDate effectiveDate;

    private Boolean active;

    public ServiceItemData toData() {
        ServiceItemData data = new ServiceItemData();
        data.setActive(this.getActive());
        data.setId(this.getId());
        if (this.getServiceType() != null) {
            data.setServiceId(this.getServiceType().getId());
            data.setServiceCode(this.getServiceType().getItemCode());
            data.setServiceName(this.getServiceType().getItemName());
        }
        data.setRate(this.getRate());

        if (this.getServicePoint() != null) {
            data.setServicePoint(this.getServicePoint().getName());
            data.setServicePointId(this.getServicePoint().getId());
        }

        data.setEffectiveDate(this.getEffectiveDate());
        return data;
    }
}
