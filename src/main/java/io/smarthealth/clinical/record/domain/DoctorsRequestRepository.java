 package io.smarthealth.clinical.record.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface DoctorsRequestRepository extends JpaRepository<DoctorRequest, Long> ,JpaSpecificationExecutor<DoctorRequest>{
//   Optional<DoctorRequest> findByRequestType (final String serviceCode, Pageable page);
}
