/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DischargeDiagnosis {

    private Long id;
    private String patientNumber;
    private String patientName;
    private String admissionNumber;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate diagnosisDate;
    private String code;
    private String description;
    private String certainty;
    private String diagnosisOrder;
    private String remarks;
    private Boolean condition;
    private String doctor;
    private String doneBy;
}
