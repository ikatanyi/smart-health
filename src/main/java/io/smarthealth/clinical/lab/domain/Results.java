/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kent
 */
@Data
@Entity
@Table(name = "patient_lab_results")
public class Results extends Identifiable {

    private Long id;
    @ManyToOne
    @JoinColumn(name="fk_results_analyte_id")
    private Analyte analyte;
    @ManyToOne
    @JoinColumn(name="fk_results_patient_lab_test_id")
    private PatientLabTest patientLabTest;
    
    private String upperRange;
    private String lowerRange;
    private String resultValue;
    private String unit;
    private String status;
    private String comments;

}
