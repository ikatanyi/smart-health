package io.smarthealth.accounting.doctors.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface DoctorItemRepository extends JpaRepository<DoctorItem, Long>, JpaSpecificationExecutor<DoctorItem>{
    
}
