package io.smarthealth.organization.person.patient.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.smarthealth.organization.person.data.PersonData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

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

    private String status;
    private String bloodType;
    private String allergyStatus;
    private boolean isAlive;

    //Additional patient data
    private String criticalInformation;
    private String basicNotes;
    private Integer age;
    private List<PatientIdentifierData> identifiers;

}
