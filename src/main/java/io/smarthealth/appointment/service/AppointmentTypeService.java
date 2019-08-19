/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.service;

import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.domain.AppointmentTypeRepository;
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
public class AppointmentTypeService {

    @Autowired
    AppointmentTypeRepository appointmentTypeRepository;

    @Transactional
    public AppointmentType createAppointmentType(AppointmentType appointmentType) {
        return appointmentTypeRepository.saveAndFlush(appointmentType);
    }

    public Page<AppointmentType> fetchAllAppointmentTypes(final Pageable pageable) {
        return appointmentTypeRepository.findAll(pageable);
    }

}
