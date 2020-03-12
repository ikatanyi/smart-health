/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.domain;

import io.smarthealth.clinical.procedure.data.PatientProcedureRegisterData;
import io.smarthealth.clinical.procedure.domain.enumeration.ProcedureTestState;
import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.lang.DateConverter;
import io.smarthealth.organization.facility.domain.Employee;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
public class PatientProcedureRegister extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_procedure_request_visit_id"))
    private Visit visit;

    @Column(nullable = false, unique = true)
    private String accessNo;

    @OneToOne
    private DoctorRequest request;

    private String patientName;
    private String patientNo;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ProcedureTestState status = ProcedureTestState.Scheduled;

    private String billNumber;
    private String transactionId; //Receipt n. or Invoice No
    private String paymentMode;
    private Double balance;
    private Double amount;
    private Double taxes;
    private Double discount;
    private Boolean isWalkin = false;

    @OneToMany(mappedBy = "patientProcedureRegister", cascade = CascadeType.ALL)
    private List<PatientProcedureTest> patientProcedureTest = new ArrayList<>();

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_procedure_register_bill_id"))
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

    public PatientProcedureRegisterData toData() {
        PatientProcedureRegisterData data = new PatientProcedureRegisterData();
        if (this.getVisit() != null && !this.isWalkin) {
            data.setVisitNumber(this.getVisit().getVisitNumber());
            data.setPatientName(this.getVisit().getPatient().getFullName());
            data.setPatientNumber(this.getVisit().getPatient().getPatientNumber());
        } else {
            data.setPatientName(this.patientNo);
            data.setPatientNumber(this.patientNo);
        }
        if (this.getRequest() != null) {
            data.setRequestId(this.getRequest().getId());
            if (this.getRequest().getRequestedBy() != null) {
                data.setRequestedBy(this.getRequest().getRequestedBy().getStaffNumber());
                data.setPhysicianName(this.getRequest().getCreatedBy());
            }
        }
        data.setAccessionNo(this.getAccessNo());
        if (this.getPatientProcedureTest() != null) {
            data.setPatientProcecedureTestData(
                    this.getPatientProcedureTest()
                            .stream()
                            .map((pscantest) -> pscantest.toData())
                            .collect(Collectors.toList())
            );
        }
        if (this.getRequest() != null) {
            data.setRequestId(this.getRequest().getId());
            data.setRequestData(DoctorRequestData.map(this.getRequest()));
        }
        data.setOrderedDate(DateConverter.toLocalDate(LocalDateTime.ofInstant(this.getCreatedOn(), ZoneOffset.UTC)));

        return data;
    }

}
