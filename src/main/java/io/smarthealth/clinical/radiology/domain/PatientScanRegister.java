/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
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
@Table(name = "patient_scan_register")
public class PatientScanRegister extends ClinicalRecord {

    @Column(nullable = false, unique = true)
    private String accessNo;
    //private String clinicalDetails;

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_request_id"))
    @OneToOne
    private DoctorRequest request;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ScanTestState status = ScanTestState.Scheduled;

    @OneToMany(mappedBy = "patientScanRegister", cascade = CascadeType.ALL)
    private List<PatientScanTest> patientScanTest = new ArrayList<>();

    @ManyToOne
    private Employee requestedBy;

    private LocalDate receivedDate;

    public void addPatientScans(List<PatientScanTest> scans) {
        for (PatientScanTest scan : scans) {
            scan.setPatientScanRegister(this);
            patientScanTest.add(scan);
        }
    }

    public void addPatientScan(PatientScanTest scan) {
        scan.setPatientScanRegister(this);
        patientScanTest.add(scan);
    }

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_bill_id"))
    @OneToOne
    private PatientBill bill;
    
}
