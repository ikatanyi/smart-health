/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ServiceData {
    @ApiModelProperty(hidden=true)
    @JsonProperty("Number")
    private String number;
    private String invoiceNumber;
    private String globalInvoiceNr;
    private String startdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private String startTime=LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    private String encounterType;
    private String codeType ="Nappi";
    private String code="INTERNAL";
    private String codeDescription;
    private Integer quantity=1;
    private Double totalAmount;
    private String reason="Non";
    private DiagnosisData diagnosis;
    private ProviderData serviceProvider;
}

