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

    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    private String admissionReason;
    private Admission.Type admissionType;
    private Long admittingDoctorId;
    private Long bedId;
    private String nhifNumber;
    private NokData nok;
    private String patientNumber;
    private PaymentDetail paymentDetail;
    private String phoneNumber;
    private String residence;
}
