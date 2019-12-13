package io.smarthealth.accounting.taxes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    Optional<Tax> findByTaxName(String name);
}
