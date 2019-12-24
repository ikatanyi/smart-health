/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.data;
 
import io.smarthealth.billing.data.BillData;
import io.smarthealth.clinical.radiology.domain.PatientRadiologyTest;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientRadiologyTestData {
    private Long id;
    private String results;
    private Long scanId;
    private RadiologyTestData testData;
    private String scanNumber;
    private ScanTestState state;
    private Long requestId;
    private DoctorRequestData requestData;    
    private String scanImagePath;    
    private BillData billData;    
    
    private static PatientRadiologyTestData map (PatientRadiologyTest scan){
        PatientRadiologyTestData entity = new PatientRadiologyTestData();
        entity.setResults(scan.getResults());        
        entity.setScanImagePath(scan.getScanImagePath());
        entity.setScanNumber(scan.getScanNumber());
        entity.setState(scan.getState());
        if(scan.getTest()!=null){
            entity.setScanId(scan.getTest().getId());
            entity.setTestData(RadiologyTestData.map(scan.getTest()));
        }  
        if(scan.getRequest()!=null){
            entity.setRequestId(scan.getRequest().getId());
            entity.setRequestData(DoctorRequestData.map(scan.getRequest()));
        }
//        if(scan.getBill()!=null)
//            entity.setBillData(PatientBillData.map(scan.getBill()));
        
        return entity;
    }
    
    private static PatientRadiologyTest map (PatientRadiologyTestData scan){
        PatientRadiologyTest entity = new PatientRadiologyTest();
        entity.setResults(scan.getResults());        
        entity.setScanImagePath(scan.getScanImagePath());
        entity.setScanNumber(scan.getScanNumber());
        entity.setState(scan.getState());
        return entity;
    }
}
