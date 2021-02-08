/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="PaymentModifier")
public class NHIFPaymentModifier {
    @JsonProperty("Type")
    private String type = "5";
    @JsonProperty("NHIF_Member_Nr")
    private String nhifMemberNr ="0" ;
    @JsonProperty("NHIF_Contributor_Nr")
    private String nhifContributorNr = "0";
    @JsonProperty("NHIF_Employer_Code")
    private String nhifEmployerCode = "0";
    @JsonProperty("NHIF_Site_Nr")
    private String nhifSiteNr = "0";
    @JsonProperty("NHIF_Patient_Relation")
    private String nhifPatientRelation="0";
    @JsonProperty("Diagnosis_Code")
    private String diagnosisCode = "ICD10";
    @JsonProperty("Admit_Date")
    private String admitDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @JsonProperty("Discharge_Date")
    private String dischargeDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @JsonProperty("Days_Used")
    private String daysUsed="1";
    @JsonProperty("Amount")
    private String amount="0";
}
