/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;
 
import io.smarthealth.billing.data.BillData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import lombok.Data;


/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientProcedureTestData {
    private Long id;
    private String results;
    private String comments;
    private Long procId;
    private ProcedureTestData testData;
    private ProcedureTestState state;
    private Long requestId;
//    private DoctorRequestData requestData;    
    private BillData billData;    
    
    public static PatientProcedureTestData map (PatientProcedureTest scan){
        PatientProcedureTestData entity = new PatientProcedureTestData();
        entity.setResults(scan.getResult());        
        entity.setState(scan.getStatus());
        if(scan.getProcedureTest()!=null){
            entity.setProcId(scan.getProcedureTest().getId());
            entity.setTestData(ProcedureTestData.map(scan.getProcedureTest()));
        }  
        return entity;
    }
    
    public static PatientProcedureTest map (PatientProcedureTestData scan){
        PatientProcedureTest entity = new PatientProcedureTest();
        entity.setResult(scan.getResults());        
//        entity.setScanNumber(scan.getScanNumber());
        entity.setStatus(scan.getState());
        return entity;
    }
}
