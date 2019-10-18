package io.smarthealth.accounting.taxes.domain;
  
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    Optional<Tax> findByTaxName(String name);
}
