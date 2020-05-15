/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.clinical.visit.data.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.VisitType;
import io.smarthealth.clinical.visit.domain.Visit;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.swagger.annotations.ApiModelProperty;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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
@Data
public class reportVisitData {

    private String visitNumber;
    @NotBlank
    private String patientNumber;
    private String patientName;
    private LocalDateTime startDatetime;
    private LocalDateTime startDate;
    private LocalDateTime stopDatetime;
    private Long duration;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private VisitType visitType;
    private Boolean scheduled;
    @NotNull
    private Long locationIdentity;
    private String servicePointName;
    private String practitionerCode;
    private String practitionerName;
    private String comments;
    @Enumerated(EnumType.STRING)
    private VisitEnum.PaymentMethod paymentMethod;
    private String paymentMode;
    @Enumerated(EnumType.STRING)
    private VisitEnum.ServiceType serviceType;
    private Boolean consultation;
    private Boolean procedure;
    private Boolean radiology;
    private Boolean triage;
    private Boolean laboratory;
    private Boolean pharmacy;
    private Boolean other;

    private Long itemToBill;

    public static reportVisitData map(Visit visitEntity) {
        reportVisitData visitDTO = new reportVisitData();
        visitDTO.setScheduled(visitEntity.getScheduled());
        visitDTO.setStartDatetime(visitEntity.getStartDatetime());
        visitDTO.setStatus(visitEntity.getStatus());
        visitDTO.setStopDatetime(visitEntity.getStopDatetime());
        visitDTO.setVisitNumber(visitEntity.getVisitNumber());
        visitDTO.setVisitType(visitEntity.getVisitType());
        visitDTO.setPaymentMethod(visitEntity.getPaymentMethod());
        visitDTO.setComments(visitEntity.getComments());
        visitDTO.setStartDate(visitEntity.getStartDatetime());
        visitDTO.setServiceType(visitEntity.getServiceType());        
        visitDTO.setConsultation(Boolean.FALSE);
        visitDTO.setLaboratory(Boolean.FALSE);
        visitDTO.setRadiology(Boolean.FALSE);
        visitDTO.setTriage(Boolean.FALSE);
        visitDTO.setProcedure(Boolean.FALSE);
        visitDTO.setOther(Boolean.FALSE);
        
        if (visitEntity.getPatient() != null) {
            visitDTO.setPatientName(visitEntity.getPatient().getFullName());
            visitDTO.setPatientNumber(visitEntity.getPatient().getPatientNumber());
        }
        if (visitEntity.getHealthProvider() != null) {
            visitDTO.setPractitionerCode(visitEntity.getHealthProvider().getStaffNumber());
            visitDTO.setPractitionerName(visitEntity.getHealthProvider().getFullName());
        }
        return visitDTO;
    }

}
