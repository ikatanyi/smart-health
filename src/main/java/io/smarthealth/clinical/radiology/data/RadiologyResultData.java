package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.laboratory.data.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class RadiologyResultData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String patientNo;
    @ApiModelProperty(required = false, hidden = true)
    private String patientName;
    private Gender gender;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate visitDate;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime resultsDate;
    @ApiModelProperty(required = false, hidden = true)
    private String scanNumber;
    private Long PatientScanRegisterId;
    private Long testId;
    @ApiModelProperty(required = false, hidden = true)
    private String testCode;
    @ApiModelProperty(required = false, hidden = true)
    private String testName;
    private String status;
    private String templateNotes;
    private String comments;
    private String imagePath;
    private Boolean voided = Boolean.FALSE;
}
