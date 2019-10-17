package io.smarthealth.clinical.lab.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import io.smarthealth.clinical.visit.validation.constraints.CheckValidVisit;
import java.util.List;
import org.smarthealth.patient.validation.constraints.ValidIdentifier;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientTestData {

    public enum State {
        Pending,
        Completed,
        Cancelled
    }

    private Long id;
    @ValidIdentifier
    private String patientNumber;
    @CheckValidVisit
    private String visitNumber;
    private String code;
    private String testName;
    private String clinicalDetails;
    private String physicianId;
    private String physicianName;
    
    private List<resultsData> resultData;
    
}
