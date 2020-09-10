/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="claim_header")
public class ClaimHeader {
    @JsonProperty("Invoice_Number")
    private String invoiceNumber;
    @JsonProperty("Claim_Date")
    private String claimDate;
    @JsonProperty("Claim_Time")
    private String claimTime;
    @JsonProperty("Pool_Number")
    private String poolNumber;
    @JsonProperty("Total_Services")
    private Integer totalServices;
    @JsonProperty("Gross_Amount")
    private Double grossAmount;
    
    @JsonProperty("Provider")
    private Provider provider;
    @JsonProperty("Authorization")
    private Authorization authorization;
//    @JsonProperty("Payment_Modifiers")
    
    @JacksonXmlElementWrapper(localName="Payment_Modifiers")
        @JsonProperty("Payment_Modifier")
        private PaymentModifier paymentModifier;
        @JsonProperty("PaymentModifier")
        private NHIFPaymentModifier nhifPaymentModifier;
    
}
