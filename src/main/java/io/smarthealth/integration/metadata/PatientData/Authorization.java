package io.smarthealth.integration.metadata.PatientData;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName = "Authorization")
public class Authorization {

    @JsonProperty("Pre_Authorization_Number")
    private String preAuthorizationNumber = "12";
    @JsonProperty("Pre_Authorization_Amount")
    private String preAuthorizationAmount = "0";
}

