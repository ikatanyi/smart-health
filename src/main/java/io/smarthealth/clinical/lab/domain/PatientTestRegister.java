/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.billing.domain.PatientBill;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class PatientTestRegister extends ClinicalRecord {

    @Column(nullable = false, unique = true)
    private String accessNo;
    //private String clinicalDetails;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_test_register_request_id"))
    @OneToOne
    private DoctorRequest request;

    @OneToMany(mappedBy = "patientTestRegister", cascade = CascadeType.ALL)
    private List<PatientLabTest> patientLabTest=new ArrayList<>();

    @ManyToOne
    private Employee requestedBy;

    private LocalDate receivedDate;

    public void addPatientLabTest(List<PatientLabTest> tests) {
        for (PatientLabTest test : tests) {
            test.setPatientTestRegister(this);
            patientLabTest.add(test);
        }
    }

    public void addPatientLabTest(PatientLabTest test) {
        test.setPatientTestRegister(this);
        patientLabTest.add(test);
    }

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_test_register_bill_id"))
    @OneToOne
    private PatientBill bill;
}
