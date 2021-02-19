/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ClaimHeader{
    @JsonProperty("Invoice_Number") 
    public String invoiceNumber;
    @JsonProperty("Claim_Date") 
    public String claimDate;
    @JsonProperty("Claim_Time") 
    public String claimTime;
    @JsonProperty("Pool_Number") 
    public String poolNumber;
    @JsonProperty("Total_Services") 
    public int totalServices;
    @JsonProperty("Gross_Amount") 
    public Double grossAmount;
}
