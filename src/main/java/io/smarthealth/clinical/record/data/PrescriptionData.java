/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import io.smarthealth.clinical.record.domain.Prescription;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class PrescriptionData extends DoctorRequestData {

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

    @ApiModelProperty(required = false, hidden = true)
    private Long id;

    public static PrescriptionData map(Prescription p) {
        PrescriptionData pd = new PrescriptionData();
        pd.setAsNeeded(p.getAsNeeded());
        pd.setAsNeededCondition(p.getAsNeededCondition());
        pd.setBrandName(p.getBrandName());
        pd.setDose(p.getDose());
        pd.setDoseUnits(p.getDoseUnits());
        pd.setDosingInstructions(p.getDosingInstructions());
        pd.setDrug(p.getDrug());
        pd.setDuration(p.getDuration());
        pd.setDurationUnits(p.getDurationUnits());
        pd.setFrequency(p.getFrequency());
        pd.setFulfillerComment(p.getFulfillerComment());
        pd.setFulfillerStatus(FullFillerStatusType.valueOf(p.getFulfillerStatus()));
        pd.setId(p.getId());
        pd.setIssuedQuantity(p.getIssuedQuantity());
        pd.setItemCode(p.getItem().getItemCode());
        pd.setNotes(p.getNotes());
        pd.setNumRefills(p.getNumRefills());
        pd.setOrderDatetime(p.getOrderDatetime());
        pd.setPatientNumber(p.getPatient().getPatientNumber());
        pd.setQuantity(p.getQuantity());
        pd.setQuantityUnits(p.getQuantityUnits());
        pd.setRequestType(RequestType.valueOf(p.getRequestType()));
        pd.setRoute(p.getRoute());
        pd.setUrgency(Urgency.valueOf(p.getUrgency()));
        pd.setVisitNumber(p.getVisitNumber());
        return pd;
    }

    public static Prescription map(PrescriptionData p) {
        Prescription pd = new Prescription();
        pd.setAsNeeded(p.getAsNeeded());
        pd.setAsNeededCondition(p.getAsNeededCondition());
        pd.setBrandName(p.getBrandName());
        pd.setDose(p.getDose());
        pd.setDoseUnits(p.getDoseUnits());
        pd.setDosingInstructions(p.getDosingInstructions());
        pd.setDrug(p.getDrug());
        pd.setDuration(p.getDuration());
        pd.setDurationUnits(p.getDurationUnits());
        pd.setFrequency(p.getFrequency());
        pd.setFulfillerComment(p.getFulfillerComment());
        pd.setFulfillerStatus(p.getFulfillerStatus().name());
        pd.setId(p.getId());
        pd.setIssuedQuantity(p.getIssuedQuantity());
        pd.setNotes(p.getNotes());
        pd.setNumRefills(p.getNumRefills());
        pd.setOrderDatetime(p.getOrderDatetime());
        pd.setQuantity(p.getQuantity());
        pd.setQuantityUnits(p.getQuantityUnits());
        pd.setRequestType(p.getRequestType().name());
        pd.setRoute(p.getRoute());
        pd.setUrgency(p.getUrgency().name());
        pd.setVisitNumber(p.getVisitNumber());
        return pd;
    }
}
