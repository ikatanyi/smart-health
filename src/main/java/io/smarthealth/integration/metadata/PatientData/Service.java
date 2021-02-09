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
public class Service{
    @JsonProperty("Number") 
    public String number;
    @JsonProperty("Invoice_Number") 
    public String invoiceNumber;
    @JsonProperty("Global_Invoice_Nr") 
    public String globalInvoiceNr;
    @ApiModelProperty(example="yyyy-MM-dd")
    @JsonProperty("Start_Date") 
    private String startdDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @ApiModelProperty(example="HH:mm:ss")
    @JsonProperty("Start_Time") 
    private String startTime=LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));   
    @JsonProperty("Provider") 
    public SProvider provider;
    @JsonProperty("Diagnosis") 
    public Diagnosis diagnosis;
    @ApiModelProperty(example="CONSULTATION/PROCEDURE/LABORATORY ETC")
    @JsonProperty("Encounter_Type") 
    public String encounterType;
    @ApiModelProperty(example="CONS/PROC/LAB ETC")
    @JsonProperty("Code_Type") 
    public String codeType;
    @ApiModelProperty(example="ServicePoint code")
    @JsonProperty("Code") 
    public String code;
    @ApiModelProperty(example="Service description")
    @JsonProperty("Code_Description") 
    public String codeDescription;
    @JsonProperty("Quantity") 
    public int quantity;
    @JsonProperty("Total_Amount") 
    public Double totalAmount;
}
