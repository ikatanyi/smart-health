/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata.PatientData;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="Service")
@JsonPropertyOrder({ "Number", "Invoice_Number", "Global_Invoice_Nr", "Start_Date","Start_Time","Provider", "Diagnosis"})
public class Service {
    @ApiModelProperty(hidden=true)
    @JsonProperty("Number")
    private String number;

    @JsonProperty("Invoice_Number")
    private String invoiceNumber;

    @JsonProperty("Global_Invoice_Nr")
    private String globalInvoiceNr;
    
    @ApiModelProperty(example="yyyy-MM-dd")
    @JsonProperty("Start_Date")
    private String startdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    
    @ApiModelProperty(example="HH:mm:ss")
    @JsonProperty("Start_Time")
    private String startTime=LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));   
    
    @ApiModelProperty(example="CONSULTATION/PROCEDURE/LABORATORY ETC")
    @JsonProperty("Encounter_Type")
    private String encounterType;

    @ApiModelProperty(example="CONS/PROC/LAB ETC")
    @JsonProperty("Code_Type")
    private String codeType ="Nappi";

    @ApiModelProperty(example="ServicePoint code")
    @JsonProperty("Code")
    private String code="INTERNAL";
    
    @JsonProperty("Code_Description")
    @ApiModelProperty(example="Service description")
    private String codeDescription;
    
    @JsonProperty("Quantity")
    private Integer quantity=1;
    
    @JsonProperty("Total_Amount")
    private Double totalAmount;
    
    @ApiModelProperty(hidden=true)
    @JsonProperty("Reason")
    private String reason="Non";
    
    @JsonProperty("Diagnosis")
    private Diagnosis diagnosis;
    
    @ApiModelProperty(hidden=true)
    @JsonProperty("Provider")
    private SProvider serviceProvider;
}

