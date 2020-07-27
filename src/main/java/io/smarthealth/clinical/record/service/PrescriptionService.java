/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.service;

import io.smarthealth.clinical.record.domain.Prescription;
import io.smarthealth.clinical.record.domain.PrescriptionRepository;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.service.PatientService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.Waweru
 */
@Service
public class PrescriptionService {

    final PrescriptionRepository prescriptionRepository;

    final PatientService patientService;

    public PrescriptionService(PrescriptionRepository prescriptionRepository, PatientService patientService) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientService = patientService;
    }

    public Optional<Prescription> fetchPrescriptionById(final Long id) {
        return prescriptionRepository.findById(id);
    }

    public List<Prescription> createPrescription(final List<Prescription> prescription) {
        return prescriptionRepository.saveAll(prescription);
    }
    
     public List<Prescription> fetchPrescriptionByNumber(final String orderNumber) {
        List l = prescriptionRepository.findByOrderNumber(orderNumber);
        return l;
    }
    
    public Page<Prescription> fetchAllPrescriptionsByVisit(final Visit visit, final Pageable pageable){
       return  prescriptionRepository.findByVisit(visit, pageable);
    }
}
