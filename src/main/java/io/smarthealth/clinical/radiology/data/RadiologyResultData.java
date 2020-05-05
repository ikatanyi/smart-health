package io.smarthealth.clinical.radiology.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    @ApiModelProperty(required = false, hidden = true)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @ApiModelProperty(required = false, hidden = true)
    private String visitNumber;
    @ApiModelProperty(required = false, hidden = true)
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate visitDate;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate resultsDate;
    @ApiModelProperty(required = false, hidden = true)
    private String scanNumber;
    private Long testId;
    @ApiModelProperty(required = false, hidden = true)
    private String testCode;
    @ApiModelProperty(required = false, hidden = true)
    private String testName;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private String templateNotes;
    private String comments;
    @ApiModelProperty(required = false, hidden = true)
    private String imagePath;
    private Boolean voided = Boolean.FALSE;

    public RadiologyResult fromData() {
        RadiologyResult entity = new RadiologyResult();
        entity.setComments(this.getComments());
        entity.setImagePath(this.getImagePath());
        entity.setNotes(this.getTemplateNotes());
        entity.setResultsDate(this.getResultsDate());
        entity.setVoided(this.getVoided());
        return entity;
    }
}
