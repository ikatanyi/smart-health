package io.smarthealth.organization.person.patient.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.data.PersonData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author K
 */ 
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PatientData extends PersonData {

    private Long id;
    private String patientNumber;
    @Enumerated(EnumType.STRING)
    private PatientStatus status;
    private String bloodType;
    private String allergyStatus;
//    private Boolean isAlive;
    private String criticalInformation;
    private String basicNotes;
    private Integer age;
    private String visitType;
    private PortraitData portraitData;
    private String inpatientNumber;
}
