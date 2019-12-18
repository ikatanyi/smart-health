package io.smarthealth.clinical.lab.data;

import io.smarthealth.clinical.lab.domain.enumeration.LabTestState;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientResultsData {
    private Long id;
    @Enumerated(EnumType.STRING)
    private LabTestState status;
    private List<ResultsData> resultsData;   
    
}
