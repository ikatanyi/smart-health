package io.smarthealth.clinical.record.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_prescriptions")
public class Prescription extends DoctorRequest {

    private String brandName;
    private String route;
    private Double dose;
    private String doseUnits; //TODO:: create an entity for dose unit
    private Integer duration;
    private Double durationUnits;
    private Double frequency;
    private Double quantity;
    private String quantityUnits; //TODO:: create an entity for quantity unit
    private String dosingInstructions;
    private Boolean asNeeded = false;
    private String asNeededCondition;
    private Integer numRefills;
    private Double issuedQuantity;

    public Prescription() {
        this.setDrug(true);
    }

}
