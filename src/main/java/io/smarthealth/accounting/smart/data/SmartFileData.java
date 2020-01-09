/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.smart.data;

import lombok.Data;


@Data
public class SmartFileData {  
    private String nhifMemberNr;
    private String nhifContributorNr;
    private String nhifEmployerCode;
    private String nhifSiteNr;
    private String nhifPatientRelation;
    private String diagnosisCode;
    private String cardnumber;
    private String patientForename;
    private String patientDob;
    private String patientTitle;
    private String policyId;
    private String patientSurname;
    private String partnerForname;
    private String partnerSurname;
    private String medicalaidExpiry;
    private String medicalaidNumber;
    private String medicalaidPlan;
    private String medicalaidCode;
    private String globalId;
    private String dependantNumber;
    private String medicalaidScheme;
    private String hospNumber;
    private String coverNumber;
    private String description;
    private String amount;
}
