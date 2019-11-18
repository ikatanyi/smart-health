/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.data;

import io.smarthealth.accounting.billing.data.PatientBillData;
import io.smarthealth.clinical.lab.domain.PatientLabTest;
import io.smarthealth.clinical.lab.domain.PatientTestRegister;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientTestRegisterData {
    private String visitNumber;
    private String requestId;
    private String clinicalDetails;
    private String labTestNumber;   
    private String billNumber;
    private String physicianId;
    private String physicianName;    
    private DoctorRequestData requestData;    
    private List<PatientTestData> testData = new ArrayList();
    private PatientBillData billData; 
    
    public static PatientTestRegister map(PatientTestRegisterData patientregister){
        PatientTestRegister entity = new PatientTestRegister();
        entity.setClinicalDetails(patientregister.getClinicalDetails());
        entity.setLabTestNumber(patientregister.getLabTestNumber());
        if(patientregister.getBillNumber()==null){
            
        }
        return entity;
    }
    
    public static PatientTestRegisterData map(PatientTestRegister patientregister){
        PatientTestRegisterData data = new PatientTestRegisterData();
        if(patientregister.getVisit()!=null){
           data.setVisitNumber(patientregister.getVisit().getVisitNumber());
        }
        if(patientregister.getRequest()!=null){
            data.setRequestId(String.valueOf(patientregister.getRequest().getId()));
            data.setPhysicianId(patientregister.getRequest().getRequestedBy().getStaffNumber());
            data.setPhysicianName(patientregister.getRequest().getCreatedBy());            
        }
        data.setLabTestNumber(patientregister.getLabTestNumber());
        data.setClinicalDetails(patientregister.getClinicalDetails());        
        
        for(PatientLabTest ttype:patientregister.getPatientLabTest()){
            data.getTestData().add(PatientTestData.map(ttype));
        }
        if(patientregister.getBill()!=null){
            data.setBillNumber(patientregister.getBill().getBillNumber());
            data.setBillData(PatientBillData.map(patientregister.getBill()));
        }
        return data;
    }
    
}
