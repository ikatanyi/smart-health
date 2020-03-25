/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.accounting.billing.domain.PatientBill;
import io.smarthealth.clinical.radiology.data.PatientScanRegisterData;
import io.smarthealth.clinical.radiology.domain.enumeration.ScanTestState;
import io.smarthealth.clinical.record.domain.DoctorRequest;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.domain.enumeration.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "patient_scan_register")
public class PatientScanRegister extends Identifiable {

    @Column(nullable = false, unique = true)
    private String accessNo;
    
    private Boolean isWalkin;
    
    private Boolean voided;
    
    private String patientNo;
    
    private String patientName;
    
    private Double amount;
    
    private Double Discount;
    
    private String paymentMode;
    
    private Double balance;
   
    @OneToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_request_id"))
    private DoctorRequest request;
    
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_visit_id"))    
    private Visit visit;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ScanTestState status = ScanTestState.Scheduled;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "patientScanRegister", cascade = CascadeType.ALL)
    private List<PatientScanTest> patientScanTest = new ArrayList<>();

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_employee_id")) 
    private Employee requestedBy;

    private LocalDate receivedDate=LocalDate.now();
    
    private LocalDate billingDate;
    
    private LocalDateTime requestDatetime;
    
    private String transactionId;

    public void addPatientScans(List<PatientScanTest> scans) {
        for (PatientScanTest scan : scans) {
            scan.setPatientScanRegister(this);
            patientScanTest.add(scan);
        }
    }

    public void addPatientScan(PatientScanTest scan) {
        scan.setPatientScanRegister(this);
        patientScanTest.add(scan);
    }

    @JoinColumn(foreignKey = @ForeignKey(name = "fk_patient_scan_register_bill_id"))
    @OneToOne
    private PatientBill bill;
    
    
    public PatientScanRegisterData todata(){
        PatientScanRegisterData data = new PatientScanRegisterData();
        data.setAccessionNo(this.getAccessNo());
        data.setCreatedOn(LocalDate.from(this.getReceivedDate()));
        data.setIsWalkin(this.getIsWalkin());
        data.setTransactionId(this.getTransactionId());
        data.setVoided(this.getVoided());
//        data.setOrderedDate(this.);
        if(this.getPatientScanTest()!=null){
           data.setPatientScanTestData(
             this.getPatientScanTest()
                   .stream()
                   .map((x)->x.toData())
                   .collect(Collectors.toList())
           );
           
        }
        if(this.getVisit()!=null && !this.isWalkin){
            data.setPatientName(this.getVisit().getPatient().getFullName());
            data.setPatientNumber(this.getVisit().getPatient().getPatientNumber());
            data.setVisitNumber(this.getVisit().getVisitNumber());
        }
        else{
            data.setPatientName(this.patientNo);
            data.setVisitNumber(this.patientNo);
        }
        
        if(this.getRequest()!=null){
            data.setRequestedBy(this.getRequest().getRequestedBy().getFullName());
            data.setRequestId(this.getRequest().getId());
        }        
        return data;
    }
}
