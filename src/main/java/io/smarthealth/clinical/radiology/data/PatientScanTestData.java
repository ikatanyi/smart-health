/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;

import io.smarthealth.clinical.radiology.domain.PatientScanTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;
/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientScanTestData {

    private Long id;
    private RadiologyTestData radiologyTestData;
    private double testPrice;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private String imagePath;
    private PatientScanRegisterData patientScanRegisterData;

    private String result;
    private String comments;
    
    public static PatientScanTest map(PatientScanTestData patScanData){
        PatientScanTest entity = new PatientScanTest();
        entity.setId(patScanData.getId());
        entity.setComments(patScanData.getComments());
        entity.setImagePath(patScanData.getImagePath());
        entity.setQuantity(patScanData.getQuantity());
        entity.setResult(patScanData.getResult());
        entity.setStatus(patScanData.getStatus());
        entity.setTestPrice(patScanData.getTestPrice());
        return entity;
    }
    
    public static PatientScanTestData map(PatientScanTest patScan){
        PatientScanTestData entity = new PatientScanTestData();
        entity.setId(patScan.getId());
        entity.setComments(patScan.getComments());
        entity.setImagePath(patScan.getImagePath());
        entity.setQuantity(patScan.getQuantity());
        entity.setResult(patScan.getResult());
        entity.setStatus(patScan.getStatus());
        entity.setTestPrice(patScan.getTestPrice());
        if(patScan.getPatientScanRegister()!=null)
            entity.setPatientScanRegisterData(PatientScanRegisterData.map(patScan.getPatientScanRegister()));
        if(patScan.getRadiologyTest()!=null)
            entity.setRadiologyTestData(RadiologyTestData.map(patScan.getRadiologyTest()));
        return entity;
    }

}
