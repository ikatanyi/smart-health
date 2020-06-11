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


import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.laboratory.domain.enumeration.LabTestStatus;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "patient_radiology_tests")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientRadiologyTest extends ClinicalRecord{
    
    private String results;
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_radiology_test_id"))
    private RadiologyTest test;
    private String scanNumber;
    //overall test status
    @Enumerated(EnumType.STRING)
    private ScanTestState state;
    
    private String referenceNo; //payment reference
    private Boolean paid; //  test paid

    private Boolean voided = Boolean.FALSE;
    private String voidedBy;
    private LocalDateTime voidDatetime;
    
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_radiology_request_id"))
    private DoctorRequest request;    
    private String scanImagePath;    
    
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_radiology_bill_id"))
    private PatientBill bill;    
}
