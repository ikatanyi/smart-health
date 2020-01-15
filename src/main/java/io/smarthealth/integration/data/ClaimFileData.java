/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import io.smarthealth.integration.metadata.Service;
import io.smarthealth.integration.metadata.claim;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ClaimFileData {
    private String InvoiceNumber;
    private LocalDate ClaimDate = LocalDate.now();
    private LocalDateTime ClaimTime= LocalDateTime.now();
    private String PoolNumber;
    private Integer TotalServices;
    private Double GrossAmount;
    
    List<Service>services;
    
    public static claim map(ClaimFileData fileData, SmartFileData smData){
        claim claimfile = new claim();
        claimfile.getClaimHeader().setGross_Amount(fileData.getGrossAmount());
        claimfile.getClaimHeader().setInvoice_Number(fileData.getInvoiceNumber());
        claimfile.getClaimHeader().setPool_Number(fileData.getPoolNumber());
        claimfile.getClaimHeader().setTotal_Services(fileData.getTotalServices());
        claimfile.getClaimData().setDischargeNotes("Diagn");
        claimfile.getClaimData().setService(fileData.getServices());
        
        claimfile.getMember().setCard_serialnumber(smData.getCardnumber());
        claimfile.getMember().setMembership_Number(smData.getMedicalaidNumber());
        claimfile.getMember().setScheme_Code(smData.getMedicalaidCode());
        claimfile.getMember().setScheme_Plan(smData.getMedicalaidPlan());
        claimfile.getPatient().setFirstName(smData.getPatientForename());
        claimfile.getPatient().setGender(smData.getPatientTitle().equals("Mrs") ? "F" : "M");
        claimfile.getPatient().setSurname(smData.getPatientSurname());
        
        return claimfile;
        
    }
}
