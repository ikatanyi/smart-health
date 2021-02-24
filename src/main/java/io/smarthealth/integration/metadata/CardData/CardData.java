/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "AdmissionInformation")
public class CardData {
//    @JsonProperty("Admit_Notes")
//    private AdmitNotes admitNotes;
    @JsonProperty("PaymentModifiers")
    private PaymentModifiers paymentModifiers;
    @JsonProperty("A1")
    private Card card;
    @JsonProperty("A2")
    private PatientDetails Patientdetails;
    @JsonProperty("B1")
    private MedicalAid medicalAid;
    @JsonProperty("NonMemMap")
    private NonMemMap nonMemMap;
    @JsonProperty("Benefits")
    private Benefits benefits;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String diagnosis;
}
