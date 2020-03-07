package io.smarthealth.clinical.laboratory.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface LabRequestRepository extends JpaRepository<LabRequest, Long>, JpaSpecificationExecutor<LabRequest> {

    Optional<LabRequest> findByLabNumber(String labNo);
}
