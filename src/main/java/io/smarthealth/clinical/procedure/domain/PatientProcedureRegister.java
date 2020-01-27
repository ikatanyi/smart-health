/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "patient_procedure_register")
public class PatientProcedureRegister extends ClinicalRecord {

    @Column(nullable = false, unique = true)
    private String accessNo;
    //private String clinicalDetails;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_register_request_id"))
    @OneToOne
    private DoctorRequest request;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ProcedureTestState status = ProcedureTestState.Scheduled;

    @OneToMany(mappedBy = "patientProcedureRegister", cascade = CascadeType.ALL)
    private List<PatientProcedureTest> patientProcedureTest = new ArrayList<>();

    @ManyToOne
    private Employee requestedBy;

    private LocalDate receivedDate;

    public void addPatientProcedures(List<PatientProcedureTest> procs) {
        for (PatientProcedureTest proc : procs) {
            proc.setPatientProcedureRegister(this);
            patientProcedureTest.add(proc);
        }
    }

    public void addPatientProcedure(PatientProcedureTest proc) {
        proc.setPatientProcedureRegister(this);
        patientProcedureTest.add(proc);
    }

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_register_bill_id"))
    @OneToOne
    private PatientBill bill;
    
}
