package io.smarthealth.clinical.radiology.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.documents.data.DocumentData;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientScanTestData {
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    @ApiModelProperty(required=false, hidden=true)
    private String scanName;
    private String testCode;//RadiologyTest code;
    private Double testPrice;
    private Double quantity;
    private String requestedBy;
    @ApiModelProperty(required=false, hidden=true)
    private String requestedByStaffNumber;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private String imagePath;
    private Boolean done;
    private Boolean paid=Boolean.FALSE;
    private String doneBy;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime entryDateTime=LocalDateTime.now();
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidDatetime;
    private String comments;
    @ApiModelProperty(required=false, hidden=true)
    private String accessNo;
    @ApiModelProperty(required=false, hidden=true)
    private Boolean isWalkin;
    
    @ApiModelProperty(required=false, hidden=true)
    private RadiologyResultData resultData;
    @ApiModelProperty(required=false, hidden=true)
    private Long requestId;
    @ApiModelProperty(required=false, hidden=true)
    private String patientNumber;
    @ApiModelProperty(required=false, hidden=true)
    private String patientName;
    @ApiModelProperty(required=false, hidden=true)
    private String template;
    @ApiModelProperty(required=false, hidden=true)
    private Long templateId;
    @ApiModelProperty(required=false, hidden=true)
    private String templateName;
    @ApiModelProperty(required=false, hidden=true)
    private String visitNumber;
    @ApiModelProperty(required=false, hidden=true)
    private Boolean supervisorConfirmation;
    @ApiModelProperty(required=false, hidden=true)
    private LocalDate receivedDate;
    
    @ApiModelProperty(required=false, hidden=true)
    private LocalDate orderedDate;
    
    @ApiModelProperty(required=false, hidden=true)
    private String reportedBy;
    
    @ApiModelProperty(required=false, hidden=true)
    private List<RadiologyResultData> reportResultData;
    
    @ApiModelProperty(required = false, hidden = true)
    private DocumentData documentData;
    
    @ApiModelProperty(required = false, hidden = true)
    private Double totalCash=0.0;
    
    @ApiModelProperty(required = false, hidden = true)
    private Double totalInsurance=0.0;
    
    @ApiModelProperty(required = false, hidden = true)
    private String  referenceNo;
    
    @ApiModelProperty(required = false, hidden = true)
    private String  paymentMode;
    
    private PaymentMethod paymentMethod;

    private boolean requestBilled;
    
    public PatientScanTest map(){
        PatientScanTest entity = new PatientScanTest();
        entity.setComments(this.getComments());        
        entity.setQuantity(this.getQuantity());       
        entity.setStatus(this.getStatus());
        entity.setEntryDateTime(this.getEntryDateTime());
        entity.setComments(this.getComments());
        entity.setPaid(this.getPaid());
        entity.setBilled(this.isRequestBilled());
        return entity;
    }
}
