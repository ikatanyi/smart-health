package io.smarthealth.clinical.laboratory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.smarthealth.clinical.laboratory.domain.enumeration.TestStatus;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */ 
@Data 
public class LabRequestData {

    private Long id;
    private String visitNumber;

    private String patientNo;

    private String patientName;

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime requestDatetime;

    private String orderNumber; //reference doctor's request

    private String labNumber;

    private String requestedBy;
    /**
     * if is walkin pass the patient no and patient name fields
     */
    private Boolean isWalkin;

    private TestStatus status;

    private String paymentMode;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<LabRequestTestData> tests = new ArrayList<>();

}
