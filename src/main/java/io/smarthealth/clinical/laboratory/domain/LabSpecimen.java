package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabSpecimenData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "lab_specimens")
public class LabSpecimen extends Identifiable {

    private String specimen;

    public LabSpecimenData toData() {
        LabSpecimenData data = new LabSpecimenData();
        data.setSpecimen(this.specimen);
        data.setId(this.getId());
        return data;
    }
}
