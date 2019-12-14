package io.smarthealth.stock.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long>{
   
}
