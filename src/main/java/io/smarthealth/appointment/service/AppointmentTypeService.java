/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.service;

import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.domain.AppointmentTypeRepository;
import io.smarthealth.infrastructure.exception.APIException;
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

    public AppointmentType fetchAppointmentTypeById(final Long appId) {
        return appointmentTypeRepository.findById(appId).orElseThrow(() -> APIException.notFound("Appointment type identified by " + appId + " not found ", appId));
    }

    public void removeAppointmentTypeById(final Long appId) {
        try {
            appointmentTypeRepository.deleteById(appId);
        } catch (Exception e) {
            throw APIException.internalError("There was an error deleting appointment type identified by " + appId, e.getMessage());
        }
    }

}
