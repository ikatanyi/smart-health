package io.smarthealth.clinical.inpatient.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.clinical.inpatient.admission.domain.Admission;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AdmissionData {

    private Long id;
    private String patientName;
    private String patientNumber;
    private String admissionReason;
    private String admissionNo;
    private Admission.Type admissionType;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate;
    private Long admittingDoctorId;
    private String admittingDoctor;
    private Long wardId;
    private String ward;
    private Long roomId;
    private String room;
    private Long bedId;
    private String bed;
    private Admission.Status status;
    private PaymentDetail payment;

}
