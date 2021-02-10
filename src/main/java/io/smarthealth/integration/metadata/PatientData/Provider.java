/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class Provider{
    @JsonProperty("Role") 
    public String role="SP";
    @JsonProperty("Country_Code") 
    public String countryCode = "Ke";
    @JsonProperty("Group_Practice_Number") 
    public String groupPracticeNumber = "SKSP_TEST";;
    @JsonProperty("Group_Practice_Name") 
    public String groupPracticeName = "SMART_TEST";
    @JsonProperty("Practice_Number") 
    public String practiceNumber = "SKSP_TEST";;
}
