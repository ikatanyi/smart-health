/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="Member")
public class Member {
    @ApiModelProperty(hidden=true)
    @JsonProperty("Membership_Number")
    private String membershipNumber;
    
    @ApiModelProperty(hidden=true)
    @JsonProperty("Card_Serialnumber")
    private String cardSerialnumber;
    
    @ApiModelProperty(hidden=true)
    @JsonProperty("Scheme_Code")
    private String schemeCode;
    
    @ApiModelProperty(hidden=true)
    @JsonProperty("Scheme_Plan")
    private String schemePlan;

}
