package io.smarthealth.organization.person.mapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.utility.Constants.DATE_PATTERN;
import io.smarthealth.organization.person.domain.PersonAddress;
import io.smarthealth.organization.person.domain.PersonContact;
import io.smarthealth.organization.person.enumeration.Gender;
import io.smarthealth.organization.person.enumeration.MaritalStatus;
import io.smarthealth.organization.person.patient.domain.Patient;
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
@Data
public final class PatientDTO {

    public enum State {
        ACTIVE,
        LOCKED,
        CLOSED
    }
    private String title;

    private String patientNumber;
    @NotBlank
    private String givenName;
    private String middleName;
    @NotBlank
    private String surname;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private MaritalStatus maritalStatus;
    private List<AddressDTO> addressDetails;
    private List<ContactDTO> contactDetails;
//    @Valid
//    private List<ContactDetail> contactDetails;
//    private List<IdentificationCard> identifications;
    private State currentState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate registrationDate;
    private String createdBy;
    private String createdOn;
    private String lastModifiedBy;
    private String lastModifiedOn;

    public static Patient map(final PatientDTO patient) {
        Patient patientEntity = new Patient();
        patientEntity.setPatientNumber(patient.getPatientNumber());
        patientEntity.setDateOfBirth(patient.getDateOfBirth());
        patientEntity.setGender(patient.getGender().name());
        patientEntity.setGivenName(patient.getGivenName());
        patientEntity.setMaritalStatus(patient.getMaritalStatus().name());
        patientEntity.setMiddleName(patient.getMiddleName());
        patientEntity.setDateRegistered(patient.getRegistrationDate());
        patientEntity.setSurname(patient.getSurname());
        patientEntity.setTitle(patient.getTitle());

        return patientEntity;
    }

    public static PatientDTO map(final Patient patientEntity) {
        final PatientDTO patient = new PatientDTO();

        patient.setPatientNumber(patientEntity.getPatientNumber());
        patient.setTitle(patientEntity.getTitle());
        patient.setGivenName(patientEntity.getGivenName());
        patient.setMiddleName(patientEntity.getMiddleName());
        patient.setSurname(patientEntity.getSurname());
        patient.setDateOfBirth(patientEntity.getDateOfBirth());
        patient.setGender(Gender.fromValue(patientEntity.getGender()));
        patient.setMaritalStatus(MaritalStatus.valueOf(patientEntity.getMaritalStatus()));
        patient.setRegistrationDate(patientEntity.getDateRegistered());
        //patient.setAddress(AddressMapper.map(patientEntity.getAddress()));       
        patient.setCurrentState(PatientDTO.State.valueOf(patientEntity.getStatus()));

        patient.setCreatedBy(patientEntity.getCreatedBy());
        patient.setCreatedOn(patientEntity.getCreatedOn().toString());
        patient.setLastModifiedBy(patientEntity.getLastModifiedBy());
        patient.setLastModifiedOn(patientEntity.getLastModifiedOn().toString());

        return patient;
    }
}
