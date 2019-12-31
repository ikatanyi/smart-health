package io.smarthealth.accounting.pricebook.domain;

import io.smarthealth.administration.servicepoint.domain.ServicePoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>,JpaSpecificationExecutor<ServiceItem> {

    public Page<ServiceItem> findByServicePoint(ServicePoint point,Pageable page);

}
