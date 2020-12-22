package io.smarthealth.stock.item.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import io.smarthealth.stock.stores.domain.Store;

/**
 *
 * @author Simon.waweru
 */
public interface ReorderRuleRepository extends JpaRepository<ReorderRule, Long> {
   Optional<ReorderRule> findByStoreAndStockItem(Store store, Item stockItem);
}
