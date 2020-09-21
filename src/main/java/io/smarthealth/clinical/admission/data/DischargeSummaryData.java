/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.DischargeSummary;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DischargeSummaryData {
    @ApiModelProperty(hidden=true)
    private String patientNumber;
    @ApiModelProperty(hidden=true)
    private String patientName;
    private Long admissionId;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime dischargeDate = LocalDateTime.now();
    private String dischargeNo;
    private String dischargeMethod;
    private String requestedBy; //who requested for discharge
    private Long doctorId;//doctor discharging
    @ApiModelProperty(hidden=true)
    private String doctor;//doctor discharging
    private String diagnosis;
    private String otherIllness;
    private String management;
    private String investigations;
    private String instructions;
    private String clinicalSummary;
    private String recommendations;
    
    public DischargeSummary map() {
        DischargeSummary entity = new DischargeSummary();
        entity.setClinicalSummary(this.getClinicalSummary());
        entity.setDiagnosis(this.getDiagnosis());
        entity.setDischargeDate(this.getDischargeDate());
        entity.setDischargeMethod(this.getDischargeMethod());
        entity.setInstructions(this.getInstructions());
        entity.setInvestigations(this.getInvestigations());
        entity.setManagement(this.getManagement());
        entity.setOtherIllness(this.getOtherIllness());
        entity.setRecommendations(this.getRecommendations());
        entity.setRequestedBy(this.getRequestedBy());
        return entity;
    }
    
}
