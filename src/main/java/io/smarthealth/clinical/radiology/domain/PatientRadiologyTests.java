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


import io.smarthealth.clinical.record.domain.ClinicalRecord;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "patient_radiology_tests")
@Inheritance(strategy = InheritanceType.JOINED)
public class PatientRadiologyTests extends ClinicalRecord{
    
}
