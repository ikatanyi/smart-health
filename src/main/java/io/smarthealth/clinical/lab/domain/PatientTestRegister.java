/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.billing.domain.PatientBill;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn; 
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_test_register")
public class PatientTestRegister extends ClinicalRecord{
    private String LabTestNumber; 
    private String clinicalDetails;   
     
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_test_register_request_id"))
    @OneToOne
    private DoctorRequest request;    
    
//    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_test_register_labtest_id"))
    @OneToMany(mappedBy = "patientTestRegister")
    private List<PatientLabTest> patientLabTest; 
    
    public void addLabTest(PatientLabTest test){
        test.setPatientTestRegister(this);
        patientLabTest.add(test);
    }
    
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_test_register_bill_id"))
    @OneToOne
    private PatientBill bill;    
}
