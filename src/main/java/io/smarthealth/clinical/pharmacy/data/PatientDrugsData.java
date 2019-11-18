/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.pharmacy.data;

import io.smarthealth.clinical.pharmacy.domain.PatientDrugs;
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
    private Double durationUnits;
    private Double frequency;
    private Double quantity;
    private String quantityUnits; //TODO:: create an entity for quantity unit
    private String dosingInstructions;
    private Boolean asNeeded = false;
    private String asNeededCondition;
    private Integer numRefills;
    private Double issuedQuantity;

    public static PatientDrugs map(PatientDrugsData pd) {
        PatientDrugs p = new PatientDrugs();
        p.setAsNeeded(pd.getAsNeeded());
        p.setAsNeededCondition(pd.getAsNeededCondition());
        p.setBrandName(pd.getBrandName());
        p.setDose(pd.getDose());
        p.setDoseUnits(pd.getDoseUnits());
        p.setDosingInstructions(pd.getDosingInstructions());
        p.setDuration(pd.getDuration());
        p.setDurationUnits(pd.getDurationUnits());
        p.setFrequency(pd.getFrequency());
        p.setNumRefills(pd.getNumRefills());
//        p.setPrescription(pd.get);
        p.setQuantity(pd.getQuantity());
        p.setQuantityUnits(pd.getQuantityUnits());
        p.setRoute(pd.getRoute());
        System.out.println("pd.getIssuedQuantity() pd.getIssuedQuantity() "+pd.getIssuedQuantity());
        p.setIssuedQuantity(pd.getIssuedQuantity());

        return p;
    }

    public static PatientDrugsData map(PatientDrugs pd) {
        PatientDrugsData p = new PatientDrugsData();
        p.setAsNeeded(pd.getAsNeeded());
        p.setAsNeededCondition(pd.getAsNeededCondition());
        p.setBrandName(pd.getBrandName());
        p.setDose(pd.getDose());
        p.setDoseUnits(pd.getDoseUnits());
        p.setDosingInstructions(pd.getDosingInstructions());
        p.setDuration(pd.getDuration());
        p.setDurationUnits(pd.getDurationUnits());
        p.setFrequency(pd.getFrequency());
        p.setNumRefills(pd.getNumRefills());
//        p.setPrescription(pd.get);
        p.setQuantity(pd.getQuantity());
        p.setQuantityUnits(pd.getQuantityUnits());
        p.setRoute(pd.getRoute());

        return p;
    }

}
