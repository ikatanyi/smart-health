package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.AdmissionRequest;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;

@Data
public class AdmissionRequestData {

    private Long id;
    private String patientName;
    private String patientNumber;
    private String opVisitNumber;

    private String requestedByUsername;
    private Long requestedByUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime requestDate;

    private String urgency;
    private String orderNumber;
    private String notes;
    @Enumerated(EnumType.STRING)
    private FullFillerStatusType fulfillerStatus;
    private String fulfillerComment;
    private Boolean voided = Boolean.FALSE;

    private String wardName;
    private Long wardId;

    public static AdmissionRequestData map(AdmissionRequest i) {
        AdmissionRequestData o = new AdmissionRequestData();
        o.setFulfillerComment(i.getFulfillerComment());
        o.setNotes(i.getNotes());
        o.setRequestDate(i.getRequestDate());
        o.setRequestDate(i.getRequestDate());
        o.setFulfillerStatus(i.getFulfillerStatus());
        o.setPatientName(i.getPatient().getFullName());
        o.setPatientNumber(i.getPatient().getPatientNumber());
        o.setRequestedByUserId(i.getRequestedBy().getId());
        o.setRequestedByUsername(i.getRequestedBy().getUsername());
        o.setWardId(i.getWard().getId());
        o.setWardName(i.getWard().getName());
        if (i.getOpVisit() != null) {
            o.setOpVisitNumber(i.getOpVisit().getVisitNumber());
        }
        o.setUrgency(i.getUrgency());
        o.setOrderNumber(i.getOrderNumber());
        return o;
    }

}
