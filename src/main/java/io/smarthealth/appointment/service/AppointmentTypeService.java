/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.service;

import io.smarthealth.appointment.data.AppointmentTypeData;
import io.smarthealth.appointment.domain.AppointmentType;
import io.smarthealth.appointment.domain.AppointmentTypeRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import java.util.Optional;
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

    private final AppointmentTypeRepository appointmentTypeRepository;
    private final SequenceNumberService sequenceNumberService; 

    public AppointmentTypeService(AppointmentTypeRepository appointmentTypeRepository, SequenceNumberService sequenceNumberService) {
        this.appointmentTypeRepository = appointmentTypeRepository;
        this.sequenceNumberService = sequenceNumberService;
    }


    @Transactional
    public AppointmentType createAppointmentType(AppointmentTypeData data) {
        String apptNo = sequenceNumberService.next(1L, Sequences.Appointment.name());

        AppointmentType appointmentType = AppointmentTypeData.map(data);

        String appTypeNumber = data.getAppointmentTypeNumber() != null ? data.getAppointmentTypeNumber() : apptNo;
//        appointmentType.setPractitioner(practitioner);
        appointmentType.setAppointmentTypeNumber(appTypeNumber);
        return appointmentTypeRepository.save(appointmentType);
    }

    @Transactional
    public AppointmentType updateAppointmentType(Long id, AppointmentTypeData data) {
        this.fetchAppointmentTypeById(id);
        AppointmentType appointmentType = AppointmentTypeData.map(data);
        return appointmentTypeRepository.save(appointmentType);
    }

    public Page<AppointmentType> fetchAllAppointmentTypes(final Pageable pageable) {
        return appointmentTypeRepository.findAll(pageable);
    }

    public AppointmentType fetchAppointmentTypeWithNoFoundDetection(final Long appId) {
        return appointmentTypeRepository.findById(appId).orElseThrow(() -> APIException.notFound("Appointment type identified by " + appId + " not found ", appId));
    }

    public Optional<AppointmentType> fetchAppointmentTypeById(final Long appId) {
        return appointmentTypeRepository.findById(appId);
    }

    public void removeAppointmentTypeById(final Long appId) {
        try {
            appointmentTypeRepository.deleteById(appId);
        } catch (Exception e) {
            throw APIException.internalError("There was an error deleting appointment type identified by " + appId, e.getMessage());
        }
    }

    public AppointmentTypeData toData(AppointmentType appointment) {
        AppointmentTypeData data = new AppointmentTypeData();//mapper.map(appointment, AppointmentData.class);        
        data.setId(appointment.getId());
        data.setName(appointment.getName());
        data.setColor(appointment.getColor());
        data.setDuration(appointment.getDuration());
        data.setAppointmentTypeNumber(appointment.getAppointmentTypeNumber());
        return data;
    }

}
