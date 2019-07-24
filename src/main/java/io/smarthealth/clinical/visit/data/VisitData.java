/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.data;

import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.Visit.Status;
import io.smarthealth.clinical.visit.domain.Visit.VisitType;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class VisitData {

    private String visitNumber;
    private String patientNumber;
    private LocalDateTime startDatetime;
    private LocalDateTime stopDatetime;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private VisitType visitType;
    private Boolean scheduled;
    private String createdBy;

    public static Visit map(VisitData visitDTO) {
        Visit visitEntity = new Visit();
        visitEntity.setCreatedBy(visitDTO.getCreatedBy());
        visitEntity.getPatient().setPatientNumber(visitDTO.getPatientNumber());
        visitEntity.setScheduled(visitDTO.getScheduled());
        visitEntity.setStartDatetime(visitDTO.getStartDatetime());
        visitEntity.setStopDatetime(visitDTO.getStopDatetime());
        visitEntity.setVisitNumber(visitDTO.getVisitNumber());
        visitEntity.setVisitType(visitDTO.getVisitType());
        visitEntity.setStatus(visitDTO.getStatus());
        return visitEntity;
    }

    public static VisitData map(Visit visitEntity) {
        VisitData visitDTO = new VisitData();
        visitDTO.setCreatedBy(visitEntity.getCreatedBy());
        visitDTO.setPatientNumber(visitEntity.getPatient().getPatientNumber());
        visitDTO.setScheduled(visitEntity.getScheduled());
        visitDTO.setStartDatetime(visitEntity.getStartDatetime());
        visitDTO.setStatus(visitEntity.getStatus());
        visitDTO.setStopDatetime(visitEntity.getStopDatetime());
        visitDTO.setVisitNumber(visitEntity.getVisitNumber());
        visitDTO.setVisitType(visitEntity.getVisitType());
        return visitDTO;
    }

}
