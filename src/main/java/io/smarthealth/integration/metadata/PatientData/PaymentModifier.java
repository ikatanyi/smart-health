/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName = "Payment_modifier")
public class PaymentModifier {

    @JsonProperty("Type")
    private String type = "1";
    @JsonProperty("Amount")
    private String amount = "0";
    @JsonProperty("Receipt")
    private String receipt = "0";
}
