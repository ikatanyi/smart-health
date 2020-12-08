package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data 
public class LabRegisterData {

    private Long id;
    private String visitNumber;

    private String patientNo;

    private String patientName;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime requestDatetime;

    private String orderNumber; //reference doctor's request

    private String labNumber;
    private Long medicId;
    @ApiModelProperty(hidden=true, required=false)
    private String requestedBy;
    @ApiModelProperty(hidden=true, required=false)
    private String requestedByStaffNumber;
    
     private String transactionId; 
    /**
     * if is walkin pass the patient no and patient name fields
     */
    @NotNull(message = "Walk-in flag is required") 
    private Boolean isWalkin=Boolean.FALSE;

    private LabTestStatus status;

    private String paymentMode;
    
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<LabRegisterTestData> tests = new ArrayList<>();
    
//    public String getFormattedRequestDateTime(){
//        
//        return this.requestDatetime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.valueOf(Constants.DATE_TIME_PATTERN)));
//    }

}
