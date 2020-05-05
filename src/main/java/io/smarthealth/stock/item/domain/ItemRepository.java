package io.smarthealth.stock.item.domain;

import io.smarthealth.stock.item.domain.enumeration.ItemCategory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kelsas
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    Optional<Item> findByItemCode(final String itemCode);

    Page<Item> findByCategory(final ItemCategory itemCode, final Pageable pageable);

     Optional<Item> findFirstByCategory(ItemCategory category);

}
