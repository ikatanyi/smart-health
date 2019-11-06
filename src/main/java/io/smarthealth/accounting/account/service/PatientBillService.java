/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.service;

import io.smarthealth.accounting.account.domain.PatientBill;
import io.smarthealth.accounting.account.domain.PatientBillLine;
import io.smarthealth.accounting.account.domain.PatientBillLineRepository;
import io.smarthealth.accounting.account.domain.PatientBillRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.infrastructure.sequence.SequenceService;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import io.smarthealth.stock.item.domain.ItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Service
public class PatientBillService {
    
    private final PatientBillRepository patientBillRepository;
    private final PatientBillLineRepository patientBillLineRepository;
    private final PatientRepository patientRepository;
    private final VisitRepository visitRepository;
    private final ItemRepository itemRepository;
    private final SequenceService sequenceService;

    public PatientBillService(PatientBillRepository patientBillRepository, PatientBillLineRepository patientBillLineRepository, PatientRepository patientRepository, VisitRepository visitRepository, ItemRepository itemRepository, SequenceService sequenceService) {
        this.patientBillRepository = patientBillRepository;
        this.patientBillLineRepository = patientBillLineRepository;
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.itemRepository = itemRepository;
        this.sequenceService = sequenceService;
    }

    public PatientBill createPatientBill(PatientBill patientBill) {
        if (!patientBill.getBillNumber().equals("")) {
            Optional<PatientBill> patientbill = patientBillRepository.findByBillNumber(patientBill.getBillNumber());
            if (patientbill.isPresent()) {
                for (PatientBillLine line : patientBill.getBillLines()) {
                    patientbill.get().getBillLines().add(line);
                }
            }
        } else {
            String billnumber = sequenceService.nextNumber(SequenceType.BillNumber);
            String referenceNumber = sequenceService.nextNumber(SequenceType.BillNumber);
            patientBill.setBillNumber(billnumber);
            patientBill.setReferenceNumber(referenceNumber);
                      
        } 
        patientBill = patientBillRepository.save(patientBill); 
        return patientBill;
    }

    public Optional<PatientBill> findBillByBillNumber(final String billNumber) {
        return patientBillRepository.findByBillNumber(billNumber);
    }
    
    public Page<PatientBill> findBill(final String billNumber, Visit visit,String paymentMode, String referenceNumber, Pageable page) {
        return patientBillRepository.findBill(billNumber, visit, paymentMode, referenceNumber,page);
    }
    
    public void deleteBillById(Long id){
        patientBillRepository.deleteById(id);
    }
    
    public void deleteBillLineById(Long id){
        patientBillLineRepository.deleteById(id);
    }
}
