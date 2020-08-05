package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AdmissionData {

    //patient details
    @ApiModelProperty(hidden = true)
    private Long id;
    private String admissionNumber; //this should be same as visit number
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    private String patientNumber;
    @ApiModelProperty(hidden = true)
    private String patientName;

    private VisitEnum.PaymentMethod paymentMethod;
    private Long wardId;
    
    @ApiModelProperty(hidden = true)
    private String wardName;
    
    private Long roomId;
    
    @ApiModelProperty(hidden = true)
    private String roomName;
    
    private Long bedId;
    
    @ApiModelProperty(hidden = true)
    private String bedName;
    
    private BedTypeData bedType;
    private Boolean discharged = Boolean.FALSE;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate;
    private String dischargedBy;
    private VisitEnum.Status status;
    private List<CareTeamData> careTeam = new ArrayList<>();
    
    public static AdmissionData map(Admission adm){
        
    }
}
