package io.smarthealth.accounting.smart.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ExchangeLocationsRepository extends JpaRepository<ExchangeLocations, Long> {
}
