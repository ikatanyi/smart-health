package io.smarthealth.clinical.laboratory.domain;

import io.smarthealth.clinical.laboratory.data.AnalyteData;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "lab_analytes")
public class Analyte extends Identifiable {

    //Sequence | Analyte | Lower Limit | Upper Limit | Reference | Units
    //1 | HGB | 11.0 | 16.0 | 11-16.0 | g/dL 
    private String analyte;
    private String units;
    private Double lowerLimit;
    private Double upperLimit;
    private String referenceValue;
    private Integer sortKey;
    private String description;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_lab_analyte_test_id"))
    private LabTest labTest;

    public AnalyteData toData() {
        AnalyteData data = new AnalyteData();
        data.setId(this.getId());
        data.setAnalyte(this.analyte);
        data.setUnits(this.units);
        data.setLowerLimit(this.lowerLimit);
        data.setUpperLimit(this.upperLimit);
        data.setReferenceValue(this.referenceValue);
        if (this.labTest != null) {
            data.setTestId(this.labTest.getId());
            data.setTestName(this.labTest.getTestName());
        }
        if(StringUtils.isBlank(referenceValue) && this.labTest.getHasReferenceValue()){
            if(this.getLowerLimit()!=null && this.getUpperLimit()!=null){
                data.setReferenceValue(this.getLowerLimit()+" - "+this.getUpperLimit());
            }
        }
        return data;
    }
}
