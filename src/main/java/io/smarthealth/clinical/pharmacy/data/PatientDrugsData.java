/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.data;

import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class PatientDrugsData {
    private Long id;
    private Long prescriptionId;
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
    
}
