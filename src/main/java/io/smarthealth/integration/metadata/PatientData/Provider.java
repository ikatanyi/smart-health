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
@JacksonXmlRootElement(localName = "Provider")
public class Provider {

    @JsonProperty("Role")
    private String role = "SP";
    @JsonProperty("Country_Code")
    private String countryCode = "Ke";
    @JsonProperty("Group_Practice_Number")
    private String groupPracticeNumber;
    @JsonProperty("Group_Practice_Name")
    private String groupPracticeName;
}
