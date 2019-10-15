package io.smarthealth.clinical.lab.data;

import io.smarthealth.clinical.lab.data.resultsData;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.lab.domain.PatientTests;
import io.smarthealth.clinical.lab.domain.results;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
