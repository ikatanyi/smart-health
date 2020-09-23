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
@JacksonXmlRootElement(localName = "PaymentModifier")
public class PaymentModifier {
    @JsonProperty("Type")
    private String type;
    @JsonProperty("NHIF_Member_Nr")
    private String nhifMemberNr;
    @JsonProperty("NHIF_contributor_Nr")
    private String nhifContributorNr;
    @JsonProperty("NHIF_Employer_Code")
    private String nhifEmployerCode;
    @JsonProperty("NHIF_Site_Nr")
    private String nhifSiteNr;
    @JsonProperty("NHIF_Patient_Relation")
    private String nhifPatientRelation;
    @JsonProperty("Diagnosis_Code")
    private String diagnosisCode;
    @JsonProperty("Admit_Date")
    private String admitDate;
    @JsonProperty("Discharge_Date")
    private String dischargeDate;
    @JsonProperty("Days_Used")
    private String daysUsed;
    @JsonProperty("Amount")
    private String amount;
}
