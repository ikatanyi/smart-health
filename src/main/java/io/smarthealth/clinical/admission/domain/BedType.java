package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
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
@Table(name = "facility_bed_type")
public class BedType extends Identifiable {

    @Column(name = "bed_type")
    private String name;
    private String description;
    private Boolean isActive = Boolean.TRUE;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_type_bed_charge_id"))
    private BedCharge bedCharge;

    public BedTypeData toData() {
        BedTypeData data = new BedTypeData();
        data.setId(this.getId());
        data.setDescription(this.getDescription());
        data.setName(this.getName());
        data.setActive(this.getIsActive());
        if (this.getBedCharge() != null) {
            data.setRate(this.getBedCharge().getRate());
            data.setRecurrentCost(this.getBedCharge().getRecurrent());
            data.setBedChargeId(this.getBedCharge().getId());
        }
        return data;
    }
}
