package io.smarthealth.clinical.inpatient.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class CreateAdmission {

    private String patientNumber;
    private String firstName;
    private String middleName;
    private String surname;
    private String gender;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate dob;
    private String nationalId;
    private String phoneNumber;
    private String email;
    private String nhifNumber;
    private String residence;
    private NokData nok;

    private String admissionReason;
    private Admission.Type admissionType;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    private Long admittingDoctorId;
    private Long bedId;

    private PaymentDetail paymentDetail;
}
