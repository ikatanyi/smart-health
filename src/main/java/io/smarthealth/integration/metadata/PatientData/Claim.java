/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="Claim")
@JsonPropertyOrder({ "Claim_Header", "Provider","Authorization","Payment_Modifiers","Member", "Patient","Claim_Data" })
public class Claim {
    @JsonProperty("Claim_Header")
    private ClaimHeader claimHeader;
    @JsonProperty("Provider")
    private Provider provider;
    @JsonProperty("Authorization")
    private Authorization authorization;
    @JsonProperty("Payment_Modifiers")
    private PaymentModifiers paymentModifiers;
    @JsonProperty("Member")
    private Member member;
    @JsonProperty("Patient")
    private Patient patient;
    @JsonProperty("Claim_Data")
    private ClaimData claimData;
}

