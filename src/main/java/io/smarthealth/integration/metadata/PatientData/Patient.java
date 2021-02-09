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
@JacksonXmlRootElement(localName = "Patient")
public class Patient {
    @JsonProperty("Dependant")
    private String dependant = "Y";
    @JsonProperty("First_Name")
    private String firstName;
    @JsonProperty("Middle_Name")
    private String middleName="-";
    @JsonProperty("Surname")
    private String surname="-";
    @JsonProperty("Date_Of_Birth")
    private String dateOfBBirth;
    @JsonProperty("Gender")
    private String gender;
}

