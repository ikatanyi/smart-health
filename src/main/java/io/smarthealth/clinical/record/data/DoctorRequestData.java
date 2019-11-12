package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.smarthealth.stock.item.data.ItemData;
import io.swagger.annotations.ApiModelProperty;
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
        Unfulfilled,
        Cancelled,
        PartiallyFullfilled
    }
    
    public enum RequestType {
        Laboratory,
        Pharmacy,
        Radiology,
        Procedure
    }
    
    public enum Urgency {
        Low,
        Medium,
        High
    }
    private Long requestId;
    private RequestType requestType;
    @ApiModelProperty(required = false, hidden = true)
    private String patientNumber;
    @ApiModelProperty(required = false, hidden = true)
    private String visitNumber;
    @NotBlank
    @NotNull
    private String itemCode;
    private String itemName;
    
    @ApiModelProperty(required = false, hidden = true)
    private ItemData item;
    
    @ApiModelProperty(required = false, hidden = true)
    private EmployeeData employeeData;
    
    @ApiModelProperty(required = false, hidden = true)
    private LocalDateTime orderDatetime;
    private Urgency urgency;
    @ApiModelProperty(required = false, hidden = true)
    private String orderNumber;
    //private String action;
    private String notes;
    @Enumerated(EnumType.STRING)
    private FullFillerStatusType fulfillerStatus;  //this is the va
    private String fulfillerComment;
    private Boolean drug;
    @ApiModelProperty(required = false, hidden = true)
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
        doctorRequestData.setRequestId(doctorRequest.getId());
        doctorRequestData.setItemName(doctorRequest.getItem().getItemName());
        return doctorRequestData;
    }
}
   
    
