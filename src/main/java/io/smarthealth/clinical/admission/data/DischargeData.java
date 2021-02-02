package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.infrastructure.lang.Constants;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DischargeData {

    private Long id;
    private String patientNumber;
    private String patientName;
    private String gender;
    private Integer age;
    private String doctor;
    private String admissionNumber;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime admissionDate;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate;
    private String dischargeNo;
    private String dischargeMethod;
    private String dischargedBy;
    private String diagnosis;
    private String instructions;
    private String outcome;
    private LocalDate reviewDate;
    private String ward;
    private String bed;
    private String residence;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String paymentMode;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigDecimal amount=new BigDecimal(0);
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String invoiceNo="";
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String scheme="";
}
