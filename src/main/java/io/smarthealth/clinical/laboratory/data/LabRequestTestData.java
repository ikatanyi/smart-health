package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LabRequestTestData {

    private Long id;
    private Long labRequestId;
    private String orderNumber;
    private String labNumber;
    private String requestedBy;
    private Long testId;
    private String testName;
    private String testCode;
    private Long requestId; //reference requence 
    private String specimen;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime collectionDateTime;
    private String collectedBy;
    private Boolean collected; // sample collected
    private Boolean paid; //  test paid
    private String referenceNo; //payment reference
    private Boolean entered; //results entered 
    private TestStatus status;

}
