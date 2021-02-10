/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author kent
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Benefit{
    @JsonProperty("Nr") 
    public int nr;
    @JsonProperty("Description") 
    public String description;
    @JsonProperty("Amount") 
    public int amount;
    @JsonProperty("PreAuthNeeded") 
    public boolean preAuthNeeded;
    @JsonProperty("Claimable") 
    public boolean claimable;
}
