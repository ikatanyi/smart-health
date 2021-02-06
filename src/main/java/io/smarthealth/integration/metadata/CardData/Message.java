/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Ikatanyi
 */
public class Message{
    @JsonProperty("Nr") 
    public int nr;
    @JsonProperty("Date") 
    public String date;
    @JsonProperty("Text") 
    public String text;
}
