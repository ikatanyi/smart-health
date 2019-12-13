package io.smarthealth.accounting.pricebook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
public interface PriceBookRepository extends JpaRepository<PriceBook, Long>,JpaSpecificationExecutor<PriceBook>  {
  Optional<PriceBook> findByName(String name);
}
