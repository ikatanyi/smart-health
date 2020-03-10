/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.data;

import io.smarthealth.accounting.billing.data.BillData;
import io.smarthealth.clinical.procedure.domain.PatientProcedureTest;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientProcedureTestData {

    @ApiModelProperty(required = false, hidden = true)
    private Long id;
    private String results;
    private String comments;
    private String procedureName;
    @ApiModelProperty(required = false, hidden = true)
    private ProcedureData testData;
    @Enumerated(EnumType.STRING)
    private ProcedureTestState state;
    private Long requestId;
    private double testPrice;
    private double discount;
    private String paymentMode;
    private double quantity;
    @ApiModelProperty(required = false, hidden = true)
    private String medicName;
    private Long medicId;
    
    

    public static PatientProcedureTestData map(PatientProcedureTest scan) {
        PatientProcedureTestData entity = new PatientProcedureTestData();
        entity.setResults(scan.getResult());
        entity.setState(scan.getStatus());
        entity.setId(scan.getId());        
        entity.setComments(scan.getComments());
        entity.setQuantity(scan.getQuantity());
        entity.setTestPrice(scan.getTestPrice());
        if (scan.getProcedureTest() != null) {
            entity.setProcedureName(scan.getProcedureTest().getProcedureName());            
        }
        if(scan.getMedic()!=null){
            entity.setMedicId(scan.getMedic().getId());
            entity.setMedicName(scan.getMedic().getFullName());
        }
        return entity;
    }

    public static PatientProcedureTest map(PatientProcedureTestData scan) {
        PatientProcedureTest entity = new PatientProcedureTest();
        entity.setResult(scan.getResults());
        entity.setStatus(scan.getState());
        return entity;
    }
}
