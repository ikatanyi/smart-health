package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
//    @OneToMany()
    @OneToMany(mappedBy = "bedType", cascade = CascadeType.ALL)
    private List<BedCharge> bedCharges;

    public void addBedCharge(BedCharge bedCharge) {
        bedCharge.setBedType(this);
        bedCharges.add(bedCharge);
    }

    public void addBedCharges(List<BedCharge> bedCharges) {
        this.bedCharges = bedCharges;
        this.bedCharges.forEach(x -> x.setBedType(this));
    }

    public BedTypeData toData() {
        BedTypeData data = new BedTypeData();
        data.setId(this.getId());
        data.setDescription(this.getDescription());
        data.setName(this.getName());
        data.setActive(this.getIsActive());
        if (this.getBedCharges() != null) {
            data.setBedCharges(this.getBedCharges()
                    .stream()
                    .map(x -> x.toData())
                    .collect(Collectors.toList()));
        }
        return data;
    }
}
