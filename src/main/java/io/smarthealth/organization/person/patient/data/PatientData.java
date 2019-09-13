package io.smarthealth.organization.person.patient.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.smarthealth.organization.person.data.AddressData;
import io.smarthealth.organization.person.data.ContactData;
import io.smarthealth.organization.person.data.PersonData;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import io.smarthealth.organization.person.domain.enumeration.MaritalStatus;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull(message = "Patient number is a required field")
    private String patientNumber;

    private String status;
    private String bloodType;
    private String allergyStatus;
    private boolean isAlive;
    
    //Additional patient data
    private String criticalInformation;
    private String basicNotes;
    private String age;

}
