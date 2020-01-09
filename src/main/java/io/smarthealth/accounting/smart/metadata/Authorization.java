/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.smart.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="Authorization")
public class Authorization {
    private String PreAuthorizationNumber="SP";
    private String PreAuthorizationAmount="Ke";
}
