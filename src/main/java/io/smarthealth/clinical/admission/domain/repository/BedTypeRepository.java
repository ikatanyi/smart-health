package io.smarthealth.clinical.admission.domain.repository;

import io.smarthealth.clinical.admission.domain.BedType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface BedTypeRepository extends JpaRepository<BedType, Long>{
    
}
