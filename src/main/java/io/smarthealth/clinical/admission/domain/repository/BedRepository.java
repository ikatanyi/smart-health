package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface BedRepository extends JpaRepository<Bed, Long>,JpaSpecificationExecutor<Bed> {

}
