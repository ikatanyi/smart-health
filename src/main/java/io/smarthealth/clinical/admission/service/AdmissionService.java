/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.domain.Admission;
import io.smarthealth.clinical.admission.domain.repository.AdmissionRepository;
import io.smarthealth.infrastructure.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final AdmissionRepository admissionRepository;

    public Admission findAdmissionById(Long id) {
        if (id != null) {
            return admissionRepository.findById(id).orElseThrow(() -> APIException.notFound("Admission id {0} not found", id));
        } else {
            throw APIException.badRequest("Please provide admission id ", "");
        }
    }

    public Admission findAdmissionByNumber(String admissionNo) {
        if (admissionNo != null) {
            return admissionRepository.findByAdmissionNo(admissionNo).orElseThrow(() -> APIException.notFound("Admission with number {0} not found", admissionNo));
        } else {
            throw APIException.badRequest("Please provide admission number ", "");
        }
    }
}
