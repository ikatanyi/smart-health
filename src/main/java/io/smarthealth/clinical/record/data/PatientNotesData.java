/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientNotesData {

    private String chiefComplaint;
    @NotNull
    private String historyNotes; //history of present complaints
    @NotNull
    private String examinationNotes;
    private String socialHistory;

    @NotNull
    @NotBlank
    private String patientNumber;
    @NotNull
    @NotBlank
    private String visitNumber;
    private String healthProvider;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime dateRecorded;

    @ApiModelProperty(required = false, hidden = true)
    private boolean voided;

    @ApiModelProperty(required = false, hidden = true)
    private String voidedBy;
    @ApiModelProperty(required = false, hidden = true)
    private LocalDateTime voidedDate;

}
