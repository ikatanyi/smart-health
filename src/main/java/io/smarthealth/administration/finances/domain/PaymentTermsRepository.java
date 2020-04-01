package io.smarthealth.administration.finances.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface PaymentTermsRepository extends JpaRepository<PaymentTerms, Long> {

    Page<PaymentTerms> findByActiveTrue(Pageable page);

    Optional<PaymentTerms> findByTermsName(String name);
}
