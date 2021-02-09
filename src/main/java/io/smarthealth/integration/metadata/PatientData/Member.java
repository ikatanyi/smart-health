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
public class Member{
    @JsonProperty("Membership_Number") 
    public String membershipNumber;
    @JsonProperty("Scheme_Code") 
    public String schemeCode;
    @JsonProperty("Scheme_Plan") 
    public String schemePlan;
}
