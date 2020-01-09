/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.smart.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.smarthealth.accounting.smart.data.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@JacksonXmlRootElement(localName="PaymentModifier")
public class NHIFPaymentModifier {
    private String Type = "0";
    private String NHIFMemberNr ="0" ;
    private String NHIFContributorNr = "0";
    private String NHIFEmployerCode = "0";
    private String NHIFSiteNr = "0";
    private String NHIFPatientRelation="0";
    private String Diagnosis_Code = "ICD10";
    private LocalDate AdmitDate=LocalDate.now();
    private LocalDate DischargeDate=LocalDate.now();
    private String Days_Used="1";
    private String Amount="0";
}
