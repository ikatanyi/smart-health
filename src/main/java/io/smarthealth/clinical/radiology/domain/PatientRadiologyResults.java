/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.organization.person.patient.domain.Patient;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_radiology_results")
public class PatientRadiologyResults extends Auditable{
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name="fk_patient_radiology_results_id_patient_id"))
    private Patient patient; 
    private String notes;
    private String testName;
    private String comments;
    private String imagePath;
    
//    public 
    
}
