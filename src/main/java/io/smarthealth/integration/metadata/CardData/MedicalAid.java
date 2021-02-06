/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "B1")
public class MedicalAid {
    @JsonProperty("medicalaid_showpools")
    private String medicalaidShowpools;
    @JsonProperty("medicalaid_expiry")
    private String medicalaidExpiry;
    @JsonProperty("nhif_membernumber")
    private String nhifMembernumber;
    @JsonProperty("dependant_number")
    private String dependantNumber;
    @JsonProperty("medicalaid_plan")
    private String medicalaidPlan;
    @JsonProperty("medicalaid_code")
    private String medicalaidCode;
    @JsonProperty("medicalaid_rules")
    private String medicalaidRules;
    @JsonProperty("global_id")
    private String globalId;
    @JsonProperty("medicalaid_limit")
    private String medicalaidLimit;
    @JsonProperty("medicalaid_regdate")
    private String medicalaidRegdate;
    @JsonProperty("medicalaid_number")
    private String medicalaidNumber;
    @JsonProperty("medicalaid_groupstatus")
    private String medicalaidGroupstatus;
}
