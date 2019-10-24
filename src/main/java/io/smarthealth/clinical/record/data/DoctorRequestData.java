package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorRequestData {

    private String requestType;
    private String patientNumber;
    private String visitNumber;
    private Item item;
    private Employee requestedBy;
    private LocalDateTime orderDatetime;
    private String urgency;
    private String orderNumber;
    private String action;
    private String notes;
    private String fulfillerStatus;  //this is the va
    private String fulfillerComment;
    private Boolean drug;
     
}
