/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.wardprocedure.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.wardprocedure.domain.NursingNotes;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class NursingNotesData {

    @ApiModelProperty(hidden = true)
    private Long nursingNotesId;
    @ApiModelProperty(hidden = true)
    private String patientName;
    @ApiModelProperty(hidden = true)
    private String patientNumber;
    private String admissionNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime datetime;
    private String notes;
    private String status;
    private String notesBy;

    public static NursingNotesData map(NursingNotes e) {
        NursingNotesData d = new NursingNotesData();
        d.setAdmissionNumber(e.getAdmission().getAdmissionNo());
        d.setDatetime(e.getDatetime());
        d.setNotes(e.getNotes());
        d.setNotesBy(e.getNotesBy());
        d.setNursingNotesId(e.getId());
        d.setPatientName(e.getPatient().getFullName());
        d.setPatientNumber(e.getPatient().getPatientNumber());
        d.setStatus(e.getStatus());
        return d;
    }

    public static NursingNotes map(NursingNotesData d) {
        NursingNotes e = new NursingNotes();
        e.setDatetime(d.getDatetime());
        e.setNotes(d.getNotes());
        e.setNotesBy(d.getNotesBy());
        e.setStatus(d.getStatus());
        return e;
    }
}
