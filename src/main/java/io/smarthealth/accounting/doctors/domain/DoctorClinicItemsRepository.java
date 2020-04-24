/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.doctors.domain;

import io.smarthealth.administration.employeespecialization.domain.EmployeeSpecialization;
import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface DoctorClinicItemsRepository extends JpaRepository<DoctorClinicItems, Long> {

//    Optional<DoctorClinicItems> findByClinic(final EmployeeSpecialization specialization);
    
    Optional<DoctorClinicItems> findByClinicName(final String clinicName);

    Optional<DoctorClinicItems> findByServiceType(final Item specialization);
}
