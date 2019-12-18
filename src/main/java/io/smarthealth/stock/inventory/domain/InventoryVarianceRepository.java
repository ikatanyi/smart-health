package io.smarthealth.stock.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface InventoryVarianceRepository extends JpaRepository<InventoryVariance, Long>, JpaSpecificationExecutor<InventoryVariance>{

}
