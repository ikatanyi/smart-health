/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Ikatanyi
 */
// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */
public class AdmissionInformation{
    @JsonProperty("Admit_Notes") 
    public String admit_Notes;
    @JsonProperty("PaymentModifiers") 
    public PaymentModifiers paymentModifiers;
    @JsonProperty("A1") 
    public A1 a1;
    @JsonProperty("A2") 
    public A2 a2;
    @JsonProperty("A3") 
    public A3 a3;
    @JsonProperty("A4") 
    public A4 a4;
    @JsonProperty("A5") 
    public A5 a5;
    @JsonProperty("A6") 
    public A6 a6;
    @JsonProperty("A7") 
    public String a7;
    @JsonProperty("A8") 
    public A8 a8;
    @JsonProperty("B1") 
    public B1 b1;
    @JsonProperty("B2") 
    public B2 b2;
    @JsonProperty("B4") 
    public String b4;
    @JsonProperty("B5") 
    public B5 b5;
    @JsonProperty("CapLogs") 
    public String capLogs;
    @JsonProperty("E6") 
    public String e6;
    @JsonProperty("FP_Members") 
    public String fP_Members;
    @JsonProperty("MiniClaims") 
    public String miniClaims;
    @JsonProperty("NonMemMap") 
    public NonMemMap nonMemMap;
    @JsonProperty("OnceOffVaccinations") 
    public OnceOffVaccinations onceOffVaccinations;
    @JsonProperty("Benefits") 
    public Benefits benefits;
    @JsonProperty("B6") 
    public B6 b6;
    @JsonProperty("Scripts") 
    public Scripts scripts;
    @JsonProperty("RepeatableVaccinations") 
    public RepeatableVaccinations repeatableVaccinations;
}




