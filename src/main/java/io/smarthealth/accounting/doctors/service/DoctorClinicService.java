/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.doctors.service;

import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import io.smarthealth.accounting.doctors.domain.DoctorClinicItemsRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Simon.waweru
 */
@Service
public class DoctorClinicService {

    @Autowired
    DoctorClinicItemsRepository clinicItemsRepository;

    @Transactional
    public DoctorClinicItems saveClinicItem(final DoctorClinicItems clinicItems) {
        return clinicItemsRepository.save(clinicItems);
    }

    public DoctorClinicItems fetchClinicById(final Long id) {
        return clinicItemsRepository.findById(id).orElseThrow(() -> APIException.notFound("Clinic identified by id  {0} not found ", id));
    }

    public Optional<DoctorClinicItems> fetchClinicByIdWithNotFoundDetection(final Long id) {
        return clinicItemsRepository.findById(id);
    }

    public Page<DoctorClinicItems> fetchClinics(final Pageable pageable) {
        return clinicItemsRepository.findAll(pageable);
    }

}
