/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="Service")
public class Service {
    private String Number;
    private String InvoiceNumber;
    private String GlobalInvoiceNr;
    private LocalDate start_Date;
    private LocalDateTime start_Time;
    private ServiceProvider serviceProvider;
    private Diagnosis diagnosis;
    private String EncounterType;
    private String codeType ="Nappi";
    private String code;
    private String CodeDescription;
    private Integer Quantity;
    private Double TotalAmount;
    private String reason="Non";
}

