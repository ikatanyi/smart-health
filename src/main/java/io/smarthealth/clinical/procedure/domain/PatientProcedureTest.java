/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

/**
 *
 * @author Kennedy.Imbenzi
 */


import io.smarthealth.clinical.procedure.data.PatientProcedureTestData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Employee;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "patient_procedure_tests")
public class PatientProcedureTest extends Identifiable{

    @ManyToOne
    private PatientProcedureRegister patientProcedureRegister;
    
    @OneToOne
    private Procedure procedureTest;
    private double testPrice;
    private double quantity;
    @Enumerated(EnumType.STRING)
    private ProcedureTestState status;
    private String result;
    private String comments;  
    @ManyToOne
    @JoinColumn(foreignKey=@ForeignKey(name="fk_patient_procedure_tests_id_employee_id"))
    private Employee medic;
    
     public  PatientProcedureTestData toData() {
        PatientProcedureTestData entity = new PatientProcedureTestData();
        entity.setResults(this.getResult());
        entity.setState(this.getStatus());
        entity.setId(this.getId());        
        entity.setComments(this.getComments());
        entity.setQuantity(this.getQuantity());
        entity.setTestPrice(this.getTestPrice());
        if (this.getProcedureTest() != null) {
            entity.setProcedureName(this.getProcedureTest().getProcedureName());            
        }
        if(this.getMedic()!=null){
            entity.setMedicId(this.getMedic().getId());
            entity.setMedicName(this.getMedic().getFullName());
        }
        if(this.getPatientProcedureRegister()!=null){
            entity.setPatientName(this.getPatientProcedureRegister().getPatientName());
            entity.setPatientNo(this.getPatientProcedureRegister().getPatientNo());
        }
        return entity;
    }
    
}
