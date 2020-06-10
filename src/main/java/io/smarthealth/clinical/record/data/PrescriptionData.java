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
    //For example .. Panadol , 2*3 for a week

    private String prescriptionNo;
    private Long prescriptionId;
    private String brandName;//Panadol
    private String route;//Oral
    private Double dose;//2
    private String doseUnits; //TODO:: create an entity for dose unit//tablets
    private Integer duration;//1
    private String durationUnits;//Week
    private String frequency;//3
    private Double quantity;//42
    private String quantityUnits; //TODO:: create an entity for quantity unit//tablets
    private String dosingInstructions;//After Meal
    private Boolean asNeeded = false;//Pharmacist Asichange instructions as prescribed by the doctor
    private String asNeededCondition;//notes additional info
    private Integer numRefills;//number of installments
    private Double issuedQuantity;//number of issued quantity

    @ApiModelProperty(required = false, hidden = true)
    private String patientName;
    @ApiModelProperty(required = false, hidden = true)
    private String patientNumber;
    @ApiModelProperty(required = false, hidden = true)
    private String itemType;

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
        pd.setFulfillerStatus(p.getFulfillerStatus());
        pd.setId(p.getId());
        pd.setIssuedQuantity(p.getIssuedQuantity());
        if (p.getPatient() != null) {
            pd.setPatientName(p.getPatient().getGivenName());
            pd.setPatientNumber(p.getPatientNumber());
        }
        if (p.getItem() != null) {
            pd.setItemType(p.getItem().getDrugForm());
        }
        //pd.set
        /*
        if (pd.get != null) {
            doctorRequestData.setItemCode(doctorRequest.getItem().getItemCode());
            doctorRequestData.setItemCostRate(doctorRequest.getItem().getCostRate());
            doctorRequestData.setItemName(doctorRequest.getItem().getItemName());
            doctorRequestData.setItemRate(doctorRequest.getItem().getRate());
        } */
        //pd.setItemData(DoctorRequestItemData.map(p.getDoctorRequestItem()));
        pd.setNotes(p.getNotes());
        pd.setNumRefills(p.getNumRefills());
        pd.setOrderDate(p.getOrderDate());
        pd.setPatientNumber(p.getPatient().getPatientNumber());
        pd.setQuantity(p.getQuantity());
        pd.setQuantityUnits(p.getQuantityUnits());
        pd.setRequestType(RequestType.valueOf(p.getRequestType().name()));
        pd.setRoute(p.getRoute());
        if (p.getUrgency() != null) {
            pd.setUrgency(Urgency.valueOf(p.getUrgency()));
        }

        pd.setVisitNumber(p.getVisitNumber());
        pd.setPrescriptionNo(p.getOrderNumber());
        pd.setPrescriptionId(p.getId());
        pd.setItemCode(p.getItem().getItemCode());
        pd.setItemName(p.getItem().getItemName());
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
        pd.setFulfillerStatus(p.getFulfillerStatus());
        pd.setId(p.getId());
        pd.setIssuedQuantity(p.getIssuedQuantity());
        pd.setNotes(p.getNotes());
        pd.setNumRefills(p.getNumRefills());
        pd.setOrderDate(p.getOrderDate());
        pd.setQuantity(p.getQuantity());
        pd.setQuantityUnits(p.getQuantityUnits());
        pd.setRequestType(pd.getRequestType());
        pd.setRoute(p.getRoute());
        if (p.getUrgency() != null) {
            pd.setUrgency(p.getUrgency().name());
        }
        pd.setVisitNumber(p.getVisitNumber());
        return pd;
    }
}
