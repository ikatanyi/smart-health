package io.smarthealth.administration.banks.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.Waweru
 */
public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByBankName(final String bankName);
}
