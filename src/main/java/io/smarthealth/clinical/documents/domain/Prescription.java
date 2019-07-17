package io.smarthealth.clinical.documents.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "patient_prescriptions")
public class Prescription extends DoctorsOrder {
    
    private String brandName;
    private String route;
    private Double dose;
    private String doseUnits; //TODO:: create an entity for dose unit
    private Integer duration;
    private String durationUnits;
    private Double frequency;
    private Double quantity;
    private String quantityUnits; //TODO:: create an entity for quantity unit
    private String dosingInstructions;
    private Boolean asNeeded = false;
    private String asNeededCondition;
    private Integer numRefills;

    public Prescription() {
        this.setDrug(true);
    }

}
