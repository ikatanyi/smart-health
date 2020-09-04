package io.smarthealth.organization.person.patient.data;

import io.smarthealth.organization.person.data.PersonIdentifierData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.data.PersonData;
import io.smarthealth.organization.person.data.PortraitData;
import io.smarthealth.organization.person.patient.data.enums.PatientStatus;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public final class PatientData extends PersonData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;

    //@NotNull(message = "Patient number is a required field")
    private String patientNumber;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;
    private String bloodType;
    private String allergyStatus;
//    private Boolean isAlive;

    private String criticalInformation;
    private String basicNotes;
    private Integer age;
    private List<PersonIdentifierData> identifiers;
    private String visitType;
    private PortraitData portraitData;
}
