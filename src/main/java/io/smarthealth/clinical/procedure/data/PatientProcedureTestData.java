/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;
 
import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    private String procedureName;
    private ProcedureTestData testData;
    @Enumerated(EnumType.STRING)
    private ProcedureTestState state;
    private Long requestId; 
    private BillData billData;    
    
    public static PatientProcedureTestData map (PatientProcedureTest scan){
        PatientProcedureTestData entity = new PatientProcedureTestData();
        entity.setResults(scan.getResult());        
        entity.setState(scan.getStatus());
       
        if(scan.getProcedureTest()!=null){
            entity.setProcId(scan.getProcedureTest().getId());
            entity.setTestData(ProcedureTestData.map(scan.getProcedureTest())); 
            entity.setProcedureName(scan.getProcedureTest().getProcedureName());
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
