/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.CardData;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Ikatanyi
 */
public class OnceOffVaccinations{
    @JsonProperty("IssuerProtected") 
    public String issuerProtected;
//    @JsonProperty("Vaccination")
//    public List<Vaccination> vaccination;
}
