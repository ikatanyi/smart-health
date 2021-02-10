/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class DiagnosisData {
    @ApiModelProperty(hidden=true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String Stage="P";
    private String Code_Type="ICD10";
    private String code="0";
}
