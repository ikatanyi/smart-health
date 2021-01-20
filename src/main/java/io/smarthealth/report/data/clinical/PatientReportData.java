/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.report.data.clinical;

import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class PatientReportData {
   
    private String patientNumber;
    private String patientName;
    private String Service;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMode;
    
    private String insuranceName;
    private String schemeName; //history of present complaints
    private String status;
    private LocalDate date;
    private String serviceType;//Consultation, Review, Others
    private String visitNumber;
}

