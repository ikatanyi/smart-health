/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Benefit")
public class Benefit {
    @JsonProperty("Nr")
    private String Nr;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("Amount")
    private String Amount;
    @JsonProperty("PreAuthNeeded")
    private String PreAuthNeeded;
    @JsonProperty("Claimable")
    private String Claimable;
}
