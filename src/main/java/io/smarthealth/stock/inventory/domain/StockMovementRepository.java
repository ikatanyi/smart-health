 package io.smarthealth.stock.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

}
