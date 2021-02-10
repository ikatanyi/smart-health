/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JsonPropertyOrder({ "Claim_Header", "Provider","Authorization","Payment_Modifiers","Member", "Patient","Claim_Data" })
public class Claim{
    @JsonProperty("Claim_Header") 
    public ClaimHeader claimHeader;
    @JsonProperty("Provider") 
    public Provider provider;
    @JsonProperty("Authorization") 
    public Authorization authorization;
    @JsonProperty("Payment_Modifiers") 
    public PaymentModifiers paymentModifiers;
    @JsonProperty("Member") 
    public Member member;
    @JsonProperty("Patient") 
    public Patient patient;
    @JsonProperty("Claim_Data") 
    public ClaimData claimData;
}

