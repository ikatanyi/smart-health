package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.LabDisciplineData;
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
@Table(name = "lab_disciplines")
public class LabDiscipline extends Identifiable {

    private String displineName;

    public LabDisciplineData toData() {
        LabDisciplineData data = new LabDisciplineData();
        data.setDisplineName(this.displineName);
        data.setId(this.getId());
        return data;
    }
}
