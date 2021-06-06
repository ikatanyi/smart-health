package io.smarthealth.clinical.visit.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.queue.data.PatientQueueData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.Status;
import io.smarthealth.clinical.visit.data.enums.VisitEnum.VisitType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;

import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
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
 * @author Simon.waweru
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class VisitData {

    private Long visitId;
    private String visitNumber;
    @NotBlank
    private String patientNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime startDatetime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime stopDatetime;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private VisitType visitType;
    private Boolean scheduled;
    //@NotNull
//    private Long servicePointIdentifier;
    @NotNull
    private Long locationIdentity;

    @ApiModelProperty(required = false, hidden = true)
    private String servicePointName;
    @ApiModelProperty(required = false, hidden = true)
    private String patientName;

    private String practitionerCode;
    private String practitionerName;
    private String comments;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private PatientData patientData;

    private PaymentDetailsData payment;

    private List<PatientQueueData> patientQueueData;
    @Enumerated(EnumType.STRING)
    private VisitEnum.ServiceType serviceType;
    private String clinic;

    private Long itemToBill;
    private int triageCategory;

    //payment details
    private String schemeName;
    private String payerName;
    private Long schemeId;
    private Long payerId;
    private Double tempRunningLimit;
    private Double runningLimit;
    private Long priceBookId;


    public static Visit map(VisitData visitDTO) {
        Visit visitEntity = new Visit();
        visitEntity.setScheduled(visitDTO.getScheduled());
        visitEntity.setStartDatetime(visitDTO.getStartDatetime());
        visitEntity.setStopDatetime(visitDTO.getStopDatetime());
        visitEntity.setVisitNumber(visitDTO.getVisitNumber());
        visitEntity.setVisitType(visitDTO.getVisitType());
        visitEntity.setStatus(visitDTO.getStatus());
        visitEntity.setPaymentMethod(visitDTO.getPaymentMethod());
        visitEntity.setComments(visitDTO.getComments());
        visitEntity.setServiceType(visitDTO.getServiceType());
        return visitEntity;
    }

    public static VisitData map(Visit visitEntity) {
        VisitData visitDTO = new VisitData();
        visitDTO.setVisitId(visitEntity.getId());
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
        visitDTO.setTriageCategory(visitEntity.getTriageCategory());
        if (visitEntity.getPatient() != null) {
            visitDTO.setPatientName(visitEntity.getPatient().getFullName());
            visitDTO.setPatientNumber(visitEntity.getPatient().getPatientNumber());
        }
        if (visitEntity.getHealthProvider() != null) {
            visitDTO.setPractitionerCode(visitEntity.getHealthProvider().getStaffNumber());
            visitDTO.setPractitionerName(visitEntity.getHealthProvider().getFullName());
        }
        if (visitEntity.getClinic() != null) {
            visitDTO.setClinic(visitEntity.getClinic().getClinicName());
        }

        if (visitEntity.getPaymentMethod().equals(PaymentMethod.Insurance)) {
            if (visitEntity.getPaymentDetails() != null) {
                visitDTO.setSchemeName(visitEntity.getPaymentDetails().getScheme().getSchemeName());
                visitDTO.setSchemeId(visitEntity.getPaymentDetails().getScheme().getId());
                if(visitEntity.getPaymentDetails().getScheme().getPayer().getPriceBook()!=null) {
                    visitDTO.setPriceBookId(visitEntity.getPaymentDetails().getScheme().getPayer().getPriceBook().getId());
                }
                visitDTO.setPayerId(visitEntity.getPaymentDetails().getPayer().getId());
                visitDTO.setPayerName(visitEntity.getPaymentDetails().getPayer().getPayerName());
                visitDTO.setRunningLimit(visitEntity.getPaymentDetails().getRunningLimit());
                visitDTO.setTempRunningLimit(visitEntity.getPaymentDetails().getTempRunningLimit());
            }
        }

        return visitDTO;
    }

}
