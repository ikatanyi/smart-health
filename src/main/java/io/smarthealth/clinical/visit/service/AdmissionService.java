/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.visit.service;

import io.smarthealth.clinical.visit.data.AdmissionDTO;
import io.smarthealth.clinical.visit.domain.AdmissionRepository;
import io.smarthealth.clinical.visit.domain.VisitRepository;
import io.smarthealth.organization.person.patient.domain.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class AdmissionService {

    @Autowired
    AdmissionRepository admissionRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    VisitRepository visitRepository;

    public AdmissionDTO fetchAdmissionHistoryByPatient(final String admissionNumber) {
        return null;
    }

    public String createAdmission(AdmissionDTO admissionDTO) {
        return null;
    }

    public String updateAdmissionDetails(String admissionNumber, AdmissionDTO admissionDTO) {
        return null;
    }

}
