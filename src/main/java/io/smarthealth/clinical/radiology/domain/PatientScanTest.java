/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;


import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 * Patient Lab Request
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_scan_test")
public class PatientScanTest extends Identifiable {

    @OneToOne
    @JoinColumn(name="fk_patient_scan_test_radiology_test_id")
    private RadiologyTest radiologyTest;
    private double testPrice;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ScanTestState status;
    private String imagePath;

    @ManyToOne
    @JoinColumn(name="fk_patient_scan_test_radiology_patient_scan_register_id")
    private PatientScanRegister patientScanRegister;

    private String result;
    private String comments;

}
