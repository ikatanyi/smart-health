package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.stock.item.data.ItemData;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorRequestData {

    public enum FullFillerStatusType {
        Fulfilled,
        Unfullfilled,
        Cancelled,
        PartiallyFullfilled
    }

    public enum RequestType {
        Lab,
        Pharmacy,
        Radiology,
        Procedure
    }

    public enum Urgency {
        Low,
        Medium,
        High
    }
    private RequestType requestType;
    private String patientNumber;
    private String visitNumber;
    @NotBlank
    @NotNull
    private String itemCode;
    private ItemData item;
    private Employee requestedBy;
    private LocalDateTime orderDatetime;
    private Urgency urgency;
    private String orderNumber;
    //private String action;
    private String notes;
    @Enumerated(EnumType.STRING)
    private FullFillerStatusType fulfillerStatus;  //this is the va
    private String fulfillerComment;
    private Boolean drug;

    private PatientData patientData;

    public static DoctorRequest map(DoctorRequestData doctorRequestData) {
        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setNotes(doctorRequestData.getNotes());
        doctorRequest.setFulfillerComment(doctorRequestData.getFulfillerComment());
        //doctorRequest.setFulfillerStatus(doctorRequestData.getFulfillerStatus().name());
        doctorRequest.setOrderNumber(doctorRequestData.getOrderNumber());
        doctorRequest.setPatientNumber(doctorRequestData.getPatientNumber());
        doctorRequest.setDrug(doctorRequestData.getDrug());
        doctorRequest.setUrgency(doctorRequestData.getUrgency().name());
        doctorRequest.setRequestType(doctorRequestData.requestType.name());
        return doctorRequest;
    }

    public static DoctorRequestData map(DoctorRequest doctorRequest) {
        DoctorRequestData doctorRequestData = new DoctorRequestData();
        doctorRequestData.setDrug(doctorRequest.getDrug());
        doctorRequestData.setFulfillerComment(doctorRequest.getFulfillerComment());
        doctorRequestData.setFulfillerStatus(DoctorRequestData.FullFillerStatusType.valueOf(doctorRequest.getFulfillerStatus()));
        doctorRequestData.setNotes(doctorRequest.getNotes());
        doctorRequestData.setOrderDatetime(doctorRequest.getOrderDatetime());
        doctorRequestData.setOrderNumber(doctorRequest.getOrderNumber());
        doctorRequestData.setRequestType(RequestType.valueOf(doctorRequest.getRequestType()));
        doctorRequestData.setUrgency(Urgency.valueOf(doctorRequest.getUrgency()));
        return doctorRequestData;
    }
}
