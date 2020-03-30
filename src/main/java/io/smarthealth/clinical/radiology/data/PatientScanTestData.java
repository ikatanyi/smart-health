/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.RadiologyResult;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientScanTestData {
    @ApiModelProperty(required=false, hidden=true)
    private Long id;
    @ApiModelProperty(required=false, hidden=true)
    private String scanName;
    private String testCode;//RadiologyTest code;
    private Double testPrice;
    private Double quantity;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private String imagePath;
    private Boolean done;
    private Boolean paid;
    private String doneBy;
    private LocalDateTime entryDateTime;
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidDatetime;
    private String comments;
    @ApiModelProperty(required=false, hidden=true)
    private RadiologyResultData resultData;
    
    public static PatientScanTest map(PatientScanTestData patScanData){
        PatientScanTest entity = new PatientScanTest();
        entity.setId(patScanData.getId());
        entity.setComments(patScanData.getComments());        
        entity.setQuantity(patScanData.getQuantity());       
        entity.setStatus(patScanData.getStatus());        
        return entity;
    }
}
