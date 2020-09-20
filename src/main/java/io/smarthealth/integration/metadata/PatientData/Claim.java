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
@JsonPropertyOrder({ "Claim_Header", "Member", "Patient","Claim_Data" })
public class Claim {
    @JsonProperty("Claim_Header")
    private ClaimHeader claimHeader;
    @JsonProperty("Member")
    private Member member;
    @JsonProperty("Patient")
    private Patient patient;
    @JsonProperty("Claim_Data")
    private ClaimData claimData;
}

