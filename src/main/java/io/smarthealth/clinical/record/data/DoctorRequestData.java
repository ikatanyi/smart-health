package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.stock.item.data.ItemData;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

}
