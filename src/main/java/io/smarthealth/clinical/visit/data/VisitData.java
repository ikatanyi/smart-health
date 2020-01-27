/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.VisitType;
import io.smarthealth.clinical.visit.domain.Visit;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VisitData {
    
    private String visitNumber;
    @NotBlank
    private String patientNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime startDatetime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime stopDatetime;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private VisitType visitType;
    private Boolean scheduled;
    @NotNull
    private Long servicePointIdentifier;
    
    @ApiModelProperty(required = false, hidden = true)
    private String servicePointName;
    
    private String practitionerCode;
    
    @Enumerated(EnumType.STRING)
    private VisitEnum.PaymentMethod paymentMethod;
    
    private PatientData patientData;
    
    private PaymentDetailsData payment;
    
    private List<PatientQueueData> patientQueueData;
    
    public static Visit map(VisitData visitDTO) {
        Visit visitEntity = new Visit();
        visitEntity.setScheduled(visitDTO.getScheduled());
        visitEntity.setStartDatetime(visitDTO.getStartDatetime());
        visitEntity.setStopDatetime(visitDTO.getStopDatetime());
        visitEntity.setVisitNumber(visitDTO.getVisitNumber());
        visitEntity.setVisitType(visitDTO.getVisitType());
        visitEntity.setStatus(visitDTO.getStatus());
        visitEntity.setPaymentMethod(visitDTO.getPaymentMethod());
        
        return visitEntity;
    }
    
    public static VisitData map(Visit visitEntity) {
        VisitData visitDTO = new VisitData();
        visitDTO.setScheduled(visitEntity.getScheduled());
        visitDTO.setStartDatetime(visitEntity.getStartDatetime());
        visitDTO.setStatus(visitEntity.getStatus());
        visitDTO.setStopDatetime(visitEntity.getStopDatetime());
        visitDTO.setVisitNumber(visitEntity.getVisitNumber());
        visitDTO.setVisitType(visitEntity.getVisitType());
        visitDTO.setPaymentMethod(visitEntity.getPaymentMethod());
        return visitDTO;
    }
    
}
