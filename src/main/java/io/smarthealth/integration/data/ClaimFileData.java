/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.integration.data;

import io.smarthealth.integration.metadata.CardData.CardData;
import io.smarthealth.integration.metadata.PatientData.*;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class ClaimFileData {
    private String InvoiceNumber;
    @ApiModelProperty(hidden=true, example="yyyy-MM-dd")
    private String ClaimDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    @ApiModelProperty(hidden=true, example="HH:mm:ss")
    private String ClaimTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    @ApiModelProperty(hidden=true)
    private String PoolNumber;
    private Double grossAmount;
    private String practiceNumber;
    private String facilityName;
    private String memberNumber;
    private String patientForeNames;
    private String surname;
    List<ServiceData>services;
    
    public Claim map(CardData smData){
        Claim claimfile = new Claim();
        ClaimHeader header = new ClaimHeader();
        ClaimData claimData = new ClaimData();
        Member member =new Member();
        Patient patient =new Patient();
        Provider provider =new Provider();
        SProvider sprovider =new SProvider();
        Authorization authorization = new Authorization();
        PaymentModifiers paymentModifies = new PaymentModifiers();
        PaymentModifier paymentModifier = new PaymentModifier();
        NHIFPaymentModifier nhifPaymentModifier = new NHIFPaymentModifier();
        
        int i=0;

        claimfile.setProvider(provider);
        header.setGrossAmount(this.getGrossAmount());
        header.setInvoiceNumber(this.getInvoiceNumber());
        header.setPoolNumber(smData.getBenefits().getBenefit().getNr());
        header.setTotalServices(this.getServices().size());
        header.setClaimDate(this.getClaimDate());
        header.setClaimTime(this.getClaimTime());
        claimfile.setAuthorization(authorization);
        paymentModifies.setPaymentModifier(paymentModifier);
        paymentModifies.setNhifPaymentModifier(nhifPaymentModifier);
        claimfile.setPaymentModifiers(paymentModifies);

        for(ServiceData service:this.getServices()){
            Service serv = new Service();
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setCode(service.getDiagnosis().getCode());
            diagnosis.setCode_Type(service.getDiagnosis().getCode_Type());

            serv.setInvoiceNumber(this.getInvoiceNumber());
            serv.setGlobalInvoiceNr(this.getInvoiceNumber());
            serv.setNumber(String.valueOf(i++));
            serv.setServiceProvider(sprovider);
            serv.setDiagnosis(diagnosis);
            serv.setEncounterType(service.getEncounterType());
            serv.setCode(service.getCode());
            serv.setCodeType(service.getCodeType());
            serv.setCodeDescription(service.getCodeDescription());
            serv.setQuantity(service.getQuantity());
            serv.setTotalAmount(service.getTotalAmount());
            claimData.getService().add(serv);
        };
        
        member.setCardSerialnumber(smData.getCard().getCardSerialnumber());
        member.setMembershipNumber(smData.getMedicalAid().getMedicalaidNumber());
        member.setSchemeCode(smData.getMedicalAid().getMedicalaidCode());
        member.setSchemePlan(smData.getMedicalAid().getMedicalaidPlan());
        
        patient.setFirstName(smData.getPatientdetails().getPatientForenames());
        patient.setSurname(smData.getPatientdetails().getPatientSurname());
        patient.setGender(smData.getPatientdetails().getPatientTitle().equals("Mrs") ? "F" : "M");
        patient.setSurname(smData.getPatientdetails().getPatientSurname());
        patient.setDateOfBBirth(smData.getPatientdetails().getPatientdob());
        
        claimfile.setClaimData(claimData);
        claimfile.setClaimHeader(header);
        claimfile.setMember(member);
        claimfile.setPatient(patient);
        return claimfile;
        
    }
}
