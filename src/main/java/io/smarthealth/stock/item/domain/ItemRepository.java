package io.smarthealth.stock.item.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kelsas
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>,JpaSpecificationExecutor<Item> {
 
    Optional<Item> findByItemCode(final String itemCode);
}
