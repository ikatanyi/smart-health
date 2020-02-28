package io.smarthealth.accounting.pricebook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface PriceListRepository extends JpaRepository<PriceList, Long>, JpaSpecificationExecutor<PriceList>{
    
}
