/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JacksonXmlRootElement(localName = "A1")
public class Card {
    @JsonProperty("card_validitystatus")
    private String cardValiditystatus;
    @JsonProperty("card_retmasscounter")
    private String cardRetmasscounter;
    @JsonProperty("card_issuername")
    private String cardIssuername;
    @JsonProperty("card_issuedate")
    private String cardIssuedate;
    @JsonProperty("card_retcounter")
    private String cardRetcounter;
    @JsonProperty("card_serialnumber")
    private String cardSerialnumber;
}
