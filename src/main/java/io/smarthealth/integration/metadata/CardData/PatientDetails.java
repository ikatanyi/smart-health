/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JacksonXmlRootElement(localName = "A2")
public class PatientDetails {
    @JsonProperty("patient_surname")
    private String patientSurname;
    @JsonProperty("patient_forenames")
    private String patientForenames;
    @JsonProperty("patient_marriagedate")
    private String patientMarriagedate;
    @JsonProperty("patient_title")
    private String patientTitle;
    @JsonProperty("patient_partnerdob")
    private String patientPartnerdob;
    @JsonProperty("patient_previoussurname")
    private String patientPrevioussurname;
    @JsonProperty("policy_currency")
    private String policyCurrency;
    @JsonProperty("policy_id")
    private String policyId;
    @JsonProperty("patient_partnerprevioussurname")
    private String patientPartnerprevioussurname;
    @JsonProperty("patient_dob")
    private String patientdob;
    @JsonProperty("patient_language")
    private String patientlanguage;
    @JsonProperty("patient_marriagestatus")
    private String patientmarriagestatus;
    @JsonProperty("policy_country")
    private String policyCountry;
}
