/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.api;

import io.smarthealth.accounting.account.data.JournalData;
import io.smarthealth.accounting.account.data.PatientBillData;
import io.smarthealth.accounting.account.data.PatientBillLineData;
import io.smarthealth.accounting.account.domain.PatientBill;
import io.smarthealth.accounting.account.domain.PatientBillLine;
import io.smarthealth.accounting.account.service.PatientBillService;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.common.PaginationUtil;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.infrastructure.utility.Pager;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Api
@RestController
@RequestMapping("/api/v1")
public class PatientBillController {
    
    private final PatientBillService billService;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;

    public PatientBillController(PatientBillService billService, PatientRepository patientRepository, VisitRepository visitRepository) {
        this.billService = billService;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
    }

    
    
    /*
    
    //api/v1/patient-billing    //create invoice and line items
    //api/v1/patient-billing/{billno}/line     
    */
    
    @PostMapping("/patient-billing")
    public ResponseEntity<?> createPatientBill(@Valid @RequestBody PatientBillData patientBillData) {
        
        List<PatientBillLine> billLines = new ArrayList();
        PatientBill patientbill = PatientBillData.map(patientBillData);
        Optional<Visit> visit = visitRepository.findByVisitNumber(patientBillData.getVisitNumber());
                //.orElseThrow(() -> APIException.notFound("Visit {0} not found.", patientBillData.getVisitNumber()));
        if(visit.isPresent()){
            patientbill.setVisit(visit.get());
            patientbill.setPatient(visit.get().getPatient());
        }
            
        for(PatientBillLineData billline:patientBillData.getBillLines()){
            billLines.add(PatientBillLineData.map(billline));
        }        
        patientbill.setBillLines(billLines);
        patientbill = billService.createPatientBill(patientbill);
        
        Pager<PatientBillData> pagers=new Pager();
        pagers.setCode("0");
        pagers.setMessage("Bill Successfully Created");
        pagers.setContent(PatientBillData.map(patientbill)); 

        return ResponseEntity.status(HttpStatus.CREATED).body(pagers);

    }
    
    @GetMapping("/patient-billing")
    public ResponseEntity<?> fetchJournalEntries(
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "visitNumber", required = false) String visitNumber,
            @RequestParam(value = "patientNumber", required = false) String patientNumber,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "billNumber", required = false) String billNumber,
            Pageable pageable
    ) {
       Visit visit1=null; 
       Optional<Visit> visit = visitRepository.findByVisitNumber(visitNumber);
       if(visit.isPresent())
           visit1=visit.get();
       Page<PatientBill> pages = billService.findBill(billNumber, visit1, paymentMode, referenceNumber, pageable);
       List<PatientBillData> billLines = new ArrayList();
       Pager<List<PatientBillData>> page = new Pager();
       page.setCode("0");
       page.setMessage(paymentMode);
       
       for(PatientBill p:pages.getContent()){
           billLines.add(PatientBillData.map(p));
       }
       page.setContent(billLines);
       return ResponseEntity.status(HttpStatus.CREATED).body(page);
    }
    
    @DeleteMapping("/patient-billing/{id}")
    public ResponseEntity<?> deleteBill(@PathVariable("id") final Long id) {
        billService.deleteBillById(id);
        return ResponseEntity.ok("200");
    }
    
    @DeleteMapping("/patient-billing/{id}/line")
    public ResponseEntity<?> deleteBillLine(@PathVariable("id") final Long id) {
        billService.deleteBillById(id);
        return ResponseEntity.ok("200");
    }
}
