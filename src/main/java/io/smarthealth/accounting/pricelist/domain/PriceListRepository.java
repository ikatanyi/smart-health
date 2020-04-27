package io.smarthealth.accounting.pricelist.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import io.smarthealth.stock.item.domain.Item;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface PriceListRepository extends JpaRepository<PriceList, Long>, JpaSpecificationExecutor<PriceList> {

    Page<PriceList> findByServicePoint(ServicePoint servicePoint, Pageable page);
    
    Page<PriceList> findByItem(Item item, Pageable page);
      
    Optional<PriceList> findByItemAndServicePoint(final Item item, final ServicePoint servicePoint);
}
