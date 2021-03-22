package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LabResultData {

    private Long id;
    private String patientNo;
    private String patientName;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate visitDate;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime resultsDate;
    private String labNumber;
    private Long labRegisterTestId;
    private Long testId;
    private String testCode;
    private String testName;
    private String analyte;
    private String resultValue;
    private String units;
    private Double lowerLimit;
    private Double upperLimit;
    private String referenceValue;
    private String status;
    private String comments;
    private String createdBy;
    private String specimen;
    private Boolean voided = Boolean.FALSE;
    private String enteredBy;
    private String validatedBy;
    private Boolean resultRead;

    public String getFormattedReferenceValue(){
        return lowerLimit!=null ? units+"("+lowerLimit+"-"+upperLimit+")" : units;
    }
}
