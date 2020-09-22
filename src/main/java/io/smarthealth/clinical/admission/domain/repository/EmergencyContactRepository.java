package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kent
 */
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long>,JpaSpecificationExecutor<EmergencyContact> {
  
}
