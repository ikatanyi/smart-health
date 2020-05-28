/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientNotesData {

    private String chiefComplaint;
    //@NotNull
    private String historyNotes; //history of present complaints
    //@NotNull
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime visitStartDate;

}
