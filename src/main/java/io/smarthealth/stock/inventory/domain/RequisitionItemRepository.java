package io.smarthealth.stock.inventory.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RequisitionItemRepository extends JpaRepository<RequisitionItem, Long>{
   
}
