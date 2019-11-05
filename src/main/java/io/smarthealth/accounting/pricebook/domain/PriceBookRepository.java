package io.smarthealth.accounting.pricebook.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Kelsas
 */
public interface PriceBookRepository extends JpaRepository<PriceBook, Long>,JpaSpecificationExecutor<PriceBook>  {
  Optional<PriceBook> findByName(String name);
}
