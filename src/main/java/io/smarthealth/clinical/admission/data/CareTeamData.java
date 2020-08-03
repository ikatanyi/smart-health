package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.CareTeamRole;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CareTeamData {

    private String patientName;
    private String patientNumber;
    private String admissionNumber;
    private Long medicId;
    private String medicName;
    private CareTeamRole role;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dateAssigned;
}
