package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface BedRepository extends JpaRepository<Bed, Long> {

}
