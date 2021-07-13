package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;

@Data
public class OPAdmissionData {
    private String visitNumber;
    private Long  admittingDoctoruserId;//admittingDoctor
    private String admissionReason;
    private Long wardId;
    private String urgency;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;

}
