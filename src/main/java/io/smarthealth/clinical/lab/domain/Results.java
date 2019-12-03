/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.lab.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import javax.persistence.Entity;
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
    private Analyte analyte;
    @ManyToOne
    private PatientLabTest patientLabTest;

//    //this appears to be redudant, but to ease fetching results by accession number, contrally to diging deeper the accession number by patientlabtest let's make our life a little bit easier by putting it here directly #handshake!
//    @ManyToOne
//    private PatientTestRegister patientTestRegister;

    private String upperRange;
    private String lowerRange;
    private String resultValue;
    private String unit;
    private String status;
    private String comments;

}
