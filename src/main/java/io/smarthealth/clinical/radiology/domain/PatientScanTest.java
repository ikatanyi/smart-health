/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;


import io.smarthealth.clinical.radiology.data.PatientScanTestData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Employee;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Lab Request
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_scan_test")
public class PatientScanTest extends Identifiable {

    @OneToOne
    private RadiologyTest radiologyTest;
    private Double testPrice;
    private Double quantity;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private Long requestId; //reference to doctor's request order number    
    private Boolean done; //results entered
    @ManyToOne
    @JoinColumn(foreignKey=@ForeignKey(name="fk_patient_scan_test_employee_id"))
    private Employee doneBy;
    private LocalDateTime entryDateTime;
    
//    private Boolean voided = Boolean.FALSE;
//    private String voidedBy;
//    private LocalDateTime voidDatetime;

    @ManyToOne
    @JoinColumn(name="fk_patient_scan_test_radiology_patient_scan_register_id")
    private PatientScanRegister patientScanRegister;
    private String comments;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_test_radiology_employee_id")) 
    private Employee medic;    
   
    @OneToOne
    private RadiologyResult radiologyResult;
    
     public PatientScanTestData toData(){
        PatientScanTestData entity = new PatientScanTestData();
        entity.setId(this.getId());
        entity.setComments(this.getComments());
        entity.setTestPrice(this.getTestPrice());
        entity.setQuantity(this.getQuantity());  
        entity.setDone(this.getDone());
        
        entity.setEntryDateTime(this.getEntryDateTime());
        entity.setStatus(this.getStatus());
//        entity.setVoidDatetime(this.getVoidDatetime());
//        entity.setVoided(this.getVoided());
//        entity.setVoidedBy(this.getVoidedBy());
        entity.setStatus(this.getStatus());
        
        if(this.getRadiologyTest()!=null){
           entity.setScanName(this.getRadiologyTest().getScanName());
           entity.setTestCode(this.getRadiologyTest().getCode());
        }
        if(this.getDoneBy()!=null){
            entity.setDoneBy(this.getDoneBy().getFullName());
        }
        if(this.getRadiologyResult()!=null){
            entity.setResultData(this.getRadiologyResult().toData());
        }
        return entity;
    }

}
