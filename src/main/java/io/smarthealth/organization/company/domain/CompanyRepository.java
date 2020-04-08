package io.smarthealth.organization.company.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface CompanyRepository extends JpaRepository<Company, String> {

    Optional<Company> findFirstByOrderByCreatedOn();
}
