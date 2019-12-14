/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

/**
 *
 * @author Kennedy.Imbenzi
 */


import io.smarthealth.billing.domain.PatientBill;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "patient_radiology_tests")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientRadiologyTest extends ClinicalRecord{
    
    private String results;
    @OneToOne
    private RadiologyTest test;
    private String scanNumber;
    private ScanTestState state;
    @OneToOne
    private DoctorRequest request;    
    private String scanImagePath;    
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_radiology_bill_id"))
    @OneToOne
    private PatientBill bill;    
}
