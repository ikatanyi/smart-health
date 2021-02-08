/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ProviderData {
    private String role = "SP";
    private String countryCode = "Ke";
    private String PracticeNumber;
    private String PracticeName;
}
