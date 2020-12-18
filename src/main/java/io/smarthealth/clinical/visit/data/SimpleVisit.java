/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class SimpleVisit {

    private Long id;
    private String patientName;
    private String patientNumber;
    private VisitEnum.VisitType visitType;
    private String visitNumber;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime visitDate;
    private String doctorName;
    private Long doctorId;
    private PaymentMethod paymentMethod;
    private VisitEnum.Status status;

    public static SimpleVisit map(Visit visit) {
        SimpleVisit sv = new SimpleVisit();
        sv.setId(visit.getId());
        if (visit.getPatient() != null) {
            sv.setPatientName(visit.getPatient().getFullName());
            sv.setPatientNumber(visit.getPatient().getPatientNumber());
        }
        sv.setVisitType(visit.getVisitType());
        sv.setVisitNumber(visit.getVisitNumber());
        sv.setVisitDate(visit.getStartDatetime());
        if (visit.getHealthProvider() != null) {
            sv.setDoctorId(visit.getHealthProvider().getId());
            sv.setDoctorName(visit.getHealthProvider().getFullName());
        }

        sv.setPaymentMethod(visit.getPaymentMethod());
        sv.setStatus(visit.getStatus());

        return sv;
    }
}
