/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.domain;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface DoctorsRequestRepository extends JpaRepository<DoctorRequest, Long> ,JpaSpecificationExecutor<DoctorRequest>{
//   Optional<DoctorRequest> findByRequestType (final String serviceCode, Pageable page);
}
