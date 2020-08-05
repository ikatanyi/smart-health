package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
    private Boolean isActive=Boolean.TRUE;
    
    public BedTypeData toData(){
        BedTypeData data = new BedTypeData();
        data.setId(this.getId());
        data.setDescription(this.getDescription());
        data.setName(this.getName());
        data.setActive(this.getIsActive());
        return data;
    }
}
