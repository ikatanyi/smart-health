package io.smarthealth.stock.item.domain;

import io.smarthealth.stock.stores.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * @author Simon.waweru
 */
public interface ReorderRuleRepository extends JpaRepository<ReorderRule, Long> {
   Optional<ReorderRule> findByStoreAndStockItem(Store store, Item stockItem);
}
