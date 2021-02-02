package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.organization.facility.data.EmployeeData;
import io.smarthealth.organization.person.patient.data.PatientData;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
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

    public enum RequestType {
        Laboratory,
        Pharmacy,
        Radiology,
        Procedure,
        Admission
/* Service point types */
//        ,Triage,
//        Consultation,
//        Laboratory,
//        Pharmacy,
//        Radiology,
//        Procedure,
//        Inpatient,
//        Theatre,
//        Optical,
//        Dental,
//        Others
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
    private double itemCostRate;
    private double itemRate;

    @ApiModelProperty(required = false, hidden = true)
    private EmployeeData employeeData;

    @ApiModelProperty(required = false, hidden = true)
    private LocalDateTime orderDate;
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
    @ApiModelProperty(required = false, hidden = true)
    private LocalDateTime createdOn;
    @ApiModelProperty(required = false, hidden = true)
    private Boolean voided;

    @ApiModelProperty(required = false, hidden = true)

    public static DoctorRequest map(DoctorRequestData doctorRequestData) {
        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setNotes(doctorRequestData.getNotes());
        doctorRequest.setFulfillerComment(doctorRequestData.getFulfillerComment());
        //doctorRequest.setFulfillerStatus(doctorRequestData.getFulfillerStatus().name());
        doctorRequest.setOrderNumber(doctorRequestData.getOrderNumber());
        doctorRequest.setPatientNumber(doctorRequestData.getPatientNumber());
        doctorRequest.setDrug(doctorRequestData.getDrug());
        doctorRequest.setUrgency(doctorRequestData.getUrgency().name());
        doctorRequest.setRequestType(doctorRequestData.requestType);

        return doctorRequest;
    }

    public static DoctorRequestData map(DoctorRequest doctorRequest) {
        DoctorRequestData doctorRequestData = new DoctorRequestData();
        doctorRequestData.setDrug(doctorRequest.getDrug());
        doctorRequestData.setFulfillerComment(doctorRequest.getFulfillerComment());
        doctorRequestData.setFulfillerStatus(doctorRequest.getFulfillerStatus());
        doctorRequestData.setNotes(doctorRequest.getNotes());
        doctorRequestData.setOrderDate(doctorRequest.getOrderDate());
        doctorRequestData.setVoided(doctorRequest.getVoided());
//        doctorRequestData.setCreatedOn(doctorRequest.getCreatedOn());

        doctorRequestData.setOrderNumber(doctorRequest.getOrderNumber());
        doctorRequestData.setRequestType(doctorRequest.getRequestType());
        if (doctorRequest.getUrgency() != null) {
            doctorRequestData.setUrgency(Urgency.valueOf(doctorRequest.getUrgency()));
        }
        doctorRequestData.setRequestId(doctorRequest.getId());
        if (doctorRequest.getItem() != null) {
            doctorRequestData.setItemCode(doctorRequest.getItem().getItemCode());
            doctorRequestData.setItemName(doctorRequest.getItem().getItemName());
        }
        doctorRequestData.setItemRate(doctorRequest.getItemRate());
        doctorRequestData.setItemCostRate(doctorRequest.getItemCostRate());
        doctorRequestData.setVisitNumber(doctorRequest.getVisitNumber());
        doctorRequestData.setPatientNumber(doctorRequest.getPatientNumber());
        if (doctorRequest.getRequestedBy() != null) {
            doctorRequestData.setEmployeeData(new EmployeeData());
            //doctorRequestData.getEmployeeData().setDepartmentCode(doctorRequest.getRequestedBy().getDepartment().getCode());
            //doctorRequestData.getEmployeeData().setDepartmentName(doctorRequest.getRequestedBy().getDepartment().getName());
//            if (!doctorRequest.getRequestedBy().getContacts().isEmpty()) {
//                doctorRequestData.getEmployeeData().setEmail(doctorRequest.getRequestedBy().getContacts().get(0).getEmail());
//                doctorRequestData.getEmployeeData().setMobile(doctorRequest.getRequestedBy().getContacts().get(0).getMobile());
//                doctorRequestData.getEmployeeData().setTelephone(doctorRequest.getRequestedBy().getContacts().get(0).getTelephone());
//            }
            doctorRequestData.getEmployeeData().setEmail(doctorRequest.getRequestedBy().getEmail());
            doctorRequestData.getEmployeeData().setMobile(doctorRequest.getRequestedBy().getPassword());
            doctorRequestData.getEmployeeData().setTelephone(doctorRequest.getRequestedBy().getPassword());

//            doctorRequestData.getEmployeeData().setEmployeeCategory(doctorRequest.getRequestedBy().getEmployeeCategory());
//            doctorRequestData.getEmployeeData().setStaffNumber(doctorRequest.getRequestedBy().getStaffNumber());
//            doctorRequestData.getEmployeeData().setStatus(doctorRequest.getRequestedBy().getStatus());
        }
        if (doctorRequest.getPatient() != null) {
            doctorRequestData.setPatientData(new PatientData());
            doctorRequestData.getPatientData().setAge(doctorRequest.getPatient().getAge());
            doctorRequestData.getPatientData().setDateOfBirth(doctorRequest.getPatient().getDateOfBirth());
            doctorRequestData.getPatientData().setPatientNumber(doctorRequest.getPatient().getPatientNumber());
            doctorRequestData.getPatientData().setFullName(doctorRequest.getPatient().getFullName());
        }

        return doctorRequestData;
    }
}
