package io.smarthealth.clinical.pharmacy.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface DispensedDrugRepository extends JpaRepository<DispensedDrug, Long>,JpaSpecificationExecutor<DispensedDrug>{
    
}
