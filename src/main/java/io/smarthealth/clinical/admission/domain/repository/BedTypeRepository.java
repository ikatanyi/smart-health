package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.BedType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface BedTypeRepository extends JpaRepository<BedType, Long>,JpaSpecificationExecutor<BedType>{
    Optional<BedType>findByNameContainingIgnoreCase(String name);
}
