package io.smarthealth.clinical.laboratory.data;

import io.smarthealth.clinical.laboratory.domain.*;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AnalyteData {

    //Sequence | Analyte | Lower Limit | Upper Limit | Reference | Units
    //1 | HGB | 11.0 | 16.0 | 11-16.0 | g/dL 
    private Long id;
    private Long testId;
    private String testName;
    private String analyte;
    private String units;
    private Double lowerLimit;
    private Double upperLimit;
    private String referenceValue;
    private Integer sortKey;
    private String description;
    
    private String testCode;

}
