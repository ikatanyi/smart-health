/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.domain;

import io.smarthealth.common.utility.APIException;
import io.smarthealth.patient.domain.Patient;
import io.smarthealth.patient.domain.PatientRepository;
import io.smarthealth.person.domain.Person;
import io.smarthealth.person.domain.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    PersonRepository personRepository;
    PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            PersonRepository personRepository,
            PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.personRepository = personRepository;
        this.patientRepository = patientRepository;
    }

    public Page<Appointment> fetchAppointmentsByPatient(final Patient patientDto) {
        //Validate if Patient exists
        if (!this.patientRepository.existsByPatientNumber(patientDto.getPatientNumber())) {
            throw APIException.notFound("Patient with the identity {0} does not exist..", patientDto.getPatientNumber());
        }
        //if patient exists return appointments
        return appointmentRepository.findByPatient(patientDto);
    }

}
