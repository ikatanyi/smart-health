package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.infrastructure.lang.Constants; 
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class LabRegisterTestData {

    private Long id;
    private Long labRegisterId;
    
    private String orderNumber;
    private String labNumber;
    private String requestedBy;
    
    private Long testId;
    private String testName;
    private String testCode;
    
    private BigDecimal testPrice;
    
    private Long requestId; //reference requence 
    private String specimen; 
    
    private Boolean collected; // sample collected
    private String collectedBy;
     @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime collectionDateTime;
    
    private Boolean paid; //  test paid
    private String referenceNo; //payment reference
    
    private Boolean entered; //results entered 
    private String enteredBy;
     @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime entryDateTime;
    
    private Boolean validated;
    private String validatedBy; 
    
     @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime validationDateTime;
    
    private LabTestStatus status;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private List<LabResultData> labResults=new ArrayList<>();
    @ApiModelProperty(required=false, hidden = true)
    private String patientNumber;
    @ApiModelProperty(required=false, hidden = true)
    private Boolean withRef;

}
