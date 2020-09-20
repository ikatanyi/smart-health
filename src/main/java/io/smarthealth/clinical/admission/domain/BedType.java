package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
//    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bed_type_bed_charge_id"))
    @OneToMany(cascade = CascadeType.ALL)
    private List<BedCharge> bedCharge;

    public BedTypeData toData() {
        BedTypeData data = new BedTypeData();
        data.setId(this.getId());
        data.setDescription(this.getDescription());
        data.setName(this.getName());
        data.setActive(this.getIsActive());
        data.setCharges(this.getBedCharge()
             .stream()
             .map(x->x.toChargeData())
             .collect(Collectors.toList()));
        return data;
    }
}
