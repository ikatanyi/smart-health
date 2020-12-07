/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.Data;
import io.smarthealth.clinical.record.domain.PatientDiagnosis;
import java.time.LocalDateTime;

/**
 *
 * @author Kelsas
 */
@Data
public class PatientDiagnosisData {

    private Long id;
    private String patientName;
    private String patientNumber;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime visitDate;
    private String doctorName;
    private Long doctorId;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime date;
    private String diagnosis;
    private String code;
    private String certainty;
    private String diagnosisOrder;
    private String notes;
    private String mCode;
    private Boolean isCondition = Boolean.FALSE;

    public static PatientDiagnosisData map(PatientDiagnosis diagnosis) {
        PatientDiagnosisData data = new PatientDiagnosisData();
        data.setId(diagnosis.getId());
        if (diagnosis.getPatient() != null) {
            data.setPatientName(diagnosis.getPatient().getFullName());
            data.setPatientNumber(diagnosis.getPatient().getPatientNumber());
        }
        if (diagnosis.getVisit() != null) {
            data.setVisitNumber(diagnosis.getVisit().getVisitNumber());
            data.setVisitDate(diagnosis.getVisit().getStartDatetime());
        }

        if (diagnosis.getHealthProvider() != null) {
            data.setDoctorId(diagnosis.getHealthProvider().getId());
            data.setDoctorName(diagnosis.getHealthProvider().getName());
        }
        data.setDate(diagnosis.getDateRecorded());
        if (diagnosis.getDiagnosis() != null) {
            data.setCode(diagnosis.getDiagnosis().getCode());
            data.setDiagnosis(diagnosis.getDiagnosis().getDescription());
        }
        data.setDiagnosisOrder(diagnosis.getDiagnosisOrder());
        data.setCertainty(diagnosis.getCertainty());
        data.setNotes(diagnosis.getNotes());
        data.setMCode(diagnosis.getMCode());
        data.setIsCondition(diagnosis.getIsCondition());

        return data;
    }
}
