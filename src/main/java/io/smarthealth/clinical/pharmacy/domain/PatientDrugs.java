/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.domain;

import io.smarthealth.clinical.record.domain.ClinicalRecord;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.record.domain.Prescription;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Entity
@Data
@Table(name = "patient_drugs")
public class PatientDrugs extends ClinicalRecord {

    private String brandName;
    private String route;
    private Double dose;
    private String doseUnits; //TODO:: create an entity for dose unit
    private Integer duration;
    private Double durationUnits;
    private Double frequency;
    private Double quantity;
    private Double issuedQuantity;
    private String quantityUnits; //TODO:: create an entity for quantity unit
    private String dosingInstructions;
    private Boolean asNeeded = false;
    private String asNeededCondition;
    private Integer numRefills;
    @ManyToOne
    private Prescription prescription;
}
