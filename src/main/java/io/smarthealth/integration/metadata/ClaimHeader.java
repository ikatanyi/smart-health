/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata;

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
    private String Invoice_Number;
    private LocalDate Claim_Date = LocalDate.now();
    private LocalDateTime Claim_Time= LocalDateTime.now();
    private String Pool_Number;
    private Integer Total_Services;
    private Double Gross_Amount;
    
    private Provider provider;
    private Authorization authorization;
    private PaymentModifiers paymentModifiers;
    
}
