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
public class PaymentModifier{
    @JsonProperty("Type") 
    public int type;
    @JsonProperty("Amount_Required") 
    public int amount_Required;
    @JsonProperty("Receipt") 
    public String receipt;
    @JsonProperty("NHIF_Member_Nr") 
    public String nHIF_Member_Nr;
    @JsonProperty("NHIF_Contributor_Nr") 
    public String nHIF_Contributor_Nr;
    @JsonProperty("NHIF_Employer_Code") 
    public String nHIF_Employer_Code;
    @JsonProperty("NHIF_Site_Nr") 
    public String nHIF_Site_Nr;
    @JsonProperty("NHIF_Patient_Relation") 
    public String nHIF_Patient_Relation;
    @JsonProperty("Diagnosis_Code") 
    public String diagnosis_Code;
    @JsonProperty("Admit_Date") 
    public String admit_Date;
    @JsonProperty("Discharge_Date") 
    public String discharge_Date;
    @JsonProperty("Days_Used") 
    public String days_Used;
    @JsonProperty("Amount") 
    public String amount;
}
