/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.appointment.domain;

import io.smarthealth.organization.facility.domain.Employee;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Simon.waweru
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    Page<Appointment> findByPatient(Patient patient, Pageable pageable);

    Page<Appointment> findByPractitioner(Employee practioneer, Pageable pageable);

    List<Appointment> findByPractitioner(Employee practioneer);

    Optional<Appointment> findByAppointmentNo(String appointmentNo);

}
