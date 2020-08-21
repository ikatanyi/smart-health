/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.NursingCarePlan;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class NursingCarePlanData {

    @ApiModelProperty(hidden = true)

    private Long nursingCarePlanId;
    @ApiModelProperty(hidden = true)
    private String patientName;
    @ApiModelProperty(hidden = true)
    private String patientNumber;
    private String admissionNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime dateTime;
    private String diagnosis;
    private String expectedOutcome;
    private String planOfCare;
    private String intervention;
    private String evaluation;
    private String doneBy;

    public static NursingCarePlanData map(NursingCarePlan e) {
        NursingCarePlanData d = new NursingCarePlanData();
        d.setAdmissionNumber(e.getAdmission().getAdmissionNo());
        d.setDateTime(e.getDatetime());
        d.setDiagnosis(e.getDiagnosis());
        d.setDoneBy(e.getDoneBy());
        d.setEvaluation(e.getEvaluation());
        d.setExpectedOutcome(e.getExpectedOutcome());
        d.setIntervention(e.getIntervention());
        d.setNursingCarePlanId(e.getId());
        d.setPatientName(e.getPatient().getFullName());
        d.setPatientNumber(e.getPatient().getPatientNumber());
        d.setPlanOfCare(e.getPlanOfCare());
        return d;
    }

    public static NursingCarePlan map(NursingCarePlanData d) {
        NursingCarePlan e = new NursingCarePlan();
        e.setDatetime(d.getDateTime());
        e.setDiagnosis(d.getDiagnosis());
        e.setDoneBy(d.getDoneBy());
        e.setEvaluation(d.getEvaluation());
        e.setExpectedOutcome(d.getExpectedOutcome());
        e.setIntervention(d.getIntervention());
        e.setPlanOfCare(d.getPlanOfCare());
        return e;
    }
}
