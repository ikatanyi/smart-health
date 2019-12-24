package io.smarthealth.administration.cashdrawer.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface CashDrawerRepository extends JpaRepository<CashDrawer, Long> {
    Optional<CashDrawer> findByName(String name);
}
