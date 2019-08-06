package io.smarthealth.stock.item.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ItemPriceRepository extends JpaRepository<ItemPrice, Long>{
    Page<ItemPrice> findByItem(final Item item, final Pageable pageable);
}
